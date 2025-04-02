package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.Adapters.BookingAdapter
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.Models.Booking
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class BookingFragment : Fragment() {

    private lateinit var bookingHistoryList: ListView
    private lateinit var bookingActive: CardView
    private lateinit var tvActiveService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var activeServiceStatus: TextView
    private lateinit var cancelButton: TextView
    private lateinit var backgroundNoBookings: TextView
    private lateinit var textView2: TextView
    private lateinit var tvActiveTime: TextView
    private lateinit var tvActiveDate: TextView
    private lateinit var btnHelp: ImageView

    private var approvedBookingId: Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("reviewSubmission", this) { _, bundle ->
            val success = bundle.getBoolean("success")
            val serviceType = bundle.getString("service_type")

            if (success) {
                Toast.makeText(requireContext(), "Review for $serviceType submitted!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)


        val sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val points = sharedPreferences.getInt("loyalty_points", 0)


        // Initialize views
        btnHelp = view.findViewById(R.id.help_button)
        bookingHistoryList = view.findViewById(R.id.booking_history_list)
        bookingActive = view.findViewById(R.id.activeBooking)
        tvActiveService = view.findViewById(R.id.tvActiveService)
        tvDate = view.findViewById(R.id.tvDate)
        tvTime = view.findViewById(R.id.tvTime)
        activeServiceStatus = view.findViewById(R.id.activeServiceStatus)
        cancelButton = view.findViewById(R.id.cancel_button)
        backgroundNoBookings = view.findViewById(R.id.background_no_bookings)
        textView2 = view.findViewById(R.id.textView2)
        tvActiveTime = view.findViewById(R.id.tvActiveTime)
        tvActiveDate = view.findViewById(R.id.tvActiveDate)

        btnHelp.setOnClickListener {
            // Create an AlertDialog.Builder
            val dialogBuilder = AlertDialog.Builder(requireContext())

            // Inflate the popup_help.xml layout
            val dialogView = layoutInflater.inflate(R.layout.popup_help, null)

            // Set the view of the dialog
            dialogBuilder.setView(dialogView)

            // Set dialog properties (optional)
            dialogBuilder.setCancelable(true) // Allow the dialog to be dismissed when clicking outside

            // Create the dialog
            val alertDialog = dialogBuilder.create()

            // Adjust the height and width
            alertDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, // Set width to match parent (or any specific value)
                900 // Set height (you can adjust this value)
            )

            // Show the dialog
            alertDialog.show()
        }



        cancelButton.setOnClickListener {
            Log.d("BookingFragment", "Cancel button clicked. Booking ID: $approvedBookingId")

            approvedBookingId?.let {
                if (it > 0) {
                    cancelBooking(it)
                } else {
                    Toast.makeText(requireContext(), "Invalid Booking ID", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(requireContext(), "No active booking to cancel", Toast.LENGTH_SHORT).show()
        }


        fetchAndDisplayBookingHistory()



        return view
    }

    private fun cancelBooking(bookingId: Int) {
        val userId = getCurrentUserId().toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "Invalid User ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Log the request before sending
        Log.d("BookingFragment", "Sending Cancel Request - booking_id: $bookingId, user_id: $userId")

        val request = ApiService.CancelBookingRequest(bookingId, userId)
        val apiService = RetrofitClient.create(requireContext())

        apiService.cancelBooking(request).enqueue(object : Callback<ApiService.CancelBookingResponse> {
            override fun onResponse(call: Call<ApiService.CancelBookingResponse>, response: Response<ApiService.CancelBookingResponse>) {
                if (response.isSuccessful) {
                    val cancelResponse = response.body()
                    if (cancelResponse?.success == true) {
                        Toast.makeText(requireContext(), "Booking Cancelled", Toast.LENGTH_SHORT).show()
                        fetchAndDisplayBookingHistory() // Refresh bookings
                    } else {
                        Toast.makeText(requireContext(), "Failed: ${cancelResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.CancelBookingResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAndDisplayBookingHistory() {
        val userId = getCurrentUserId()
        Log.d("BookingFragment", "Current User ID: $userId")

        if (userId.isEmpty()) {
            Log.e("BookingFragment", "User ID is not available. Please log in.")
            return
        }

        val bookingApiService = RetrofitClient.create(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = bookingApiService.getBookingHistoryByUserId(userId)
                call.enqueue(object : Callback<ApiService.BookingHistoryResponse> {
                    override fun onResponse(
                        call: Call<ApiService.BookingHistoryResponse>,
                        response: Response<ApiService.BookingHistoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val bookingHistoryResponse = response.body()
                            Log.d("BookingFragment", "Raw API Response: $bookingHistoryResponse")

                            if (bookingHistoryResponse != null) {
                                val bookings = bookingHistoryResponse.history.map { bookingHistory ->
                                    Booking(
                                        booking_id = bookingHistory.booking_id,
                                        service_type = bookingHistory.service_type,
                                        date_formatted = bookingHistory.date_formatted,
                                        time = bookingHistory.time ?: "Not Available",
                                        status = bookingHistory.status,
                                        is_reviewed = bookingHistory.is_reviewed
                                    )
                                }

                                Log.d("BookingFragment", "Mapped Bookings: $bookings")

                                val completedBookings = bookings.filter { it.status == "Completed" || it.status == "Rejected" || it.status == "Cancelled"}
                                val approvedBooking = bookings.find { it.status == "Approved" || it.status == "Pending" }
                                approvedBookingId = approvedBooking?.booking_id ?: 0
                                Log.d("BookingFragment", "Approved Booking ID: $approvedBookingId") // Store the ID for cancellation

                                val approvedTimeFormatted = formatTimeTo12Hour(approvedBooking?.time)

                                CoroutineScope(Dispatchers.Main).launch {
                                    if (completedBookings.isNotEmpty()) {
                                        // Set the adapter for the ListView to show completed bookings
                                        val adapter = BookingAdapter(requireContext(), completedBookings) { booking ->
                                            val reviewFormFragment = ReviewFormFragment().apply {
                                                arguments = Bundle().apply {
                                                    putInt("booking_id", booking.booking_id)
                                                    putString("service_type", booking.service_type)
                                                    putString("service_date", booking.date_formatted)
                                                    putString("service_time", booking.time)
                                                    putString("status", booking.status)
                                                }
                                            }

                                            parentFragmentManager.beginTransaction()
                                                .replace(R.id.fragment_container, reviewFormFragment)
                                                .addToBackStack(null)
                                                .commit()
                                        }
                                        bookingHistoryList.adapter = adapter
                                        backgroundNoBookings.visibility = View.GONE
                                    } else {
                                        backgroundNoBookings.visibility = View.VISIBLE
                                    }

                                    if (approvedBooking != null) {
                                        tvActiveService.text = approvedBooking.service_type
                                        tvDate.text = approvedBooking.date_formatted
                                        tvTime.text = approvedTimeFormatted
                                        activeServiceStatus.text = approvedBooking.status

                                        // Set text color based on status
                                        if (approvedBooking.status == "Pending") {
                                            activeServiceStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.pendingcolor))
                                        } else {
                                            activeServiceStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.approvedcolor)) // Default color
                                        }

                                        backgroundNoBookings.visibility = View.GONE
                                    } else {
                                        tvActiveTime.visibility = View.GONE
                                        tvActiveDate.visibility = View.GONE
                                        tvActiveService.visibility = View.GONE
                                        tvDate.visibility = View.GONE
                                        tvTime.visibility = View.GONE
                                        activeServiceStatus.visibility = View.GONE
                                        textView2.visibility = View.GONE
                                        cancelButton.visibility = View.GONE
                                        backgroundNoBookings.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                Log.e("BookingFragment", "Booking history response is null")
                            }
                        } else {
                            Log.e("BookingFragment", "Failed to fetch booking history: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ApiService.BookingHistoryResponse>, t: Throwable) {
                        Log.e("BookingFragment", "API call failed", t)
                    }
                })
            } catch (e: Exception) {
                Log.e("BookingFragment", "Exception in fetchAndDisplayBookingHistory", e)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        approvedBookingId = null // Reset the approved booking ID
        fetchAndDisplayBookingHistory()  // Refresh the booking list
    }


    private fun formatTimeTo12Hour(time: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("h a", Locale.getDefault())
            val date = inputFormat.parse(time ?: "00:00:00")
            outputFormat.format(date ?: "").uppercase(Locale.getDefault())
        } catch (e: Exception) {
            "Not Available"
        }
    }

    private fun getCurrentUserId(): String {
        val sessionManagement = SessionManagement(requireContext())
        val userId = sessionManagement.getUserId() ?: ""
        Log.d("BookingFragment", "Retrieved User ID: $userId")
        return userId
    }
}
