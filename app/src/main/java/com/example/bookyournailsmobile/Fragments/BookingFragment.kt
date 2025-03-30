package com.example.bookyournailsmobile.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
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
import java.util.*

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

    private var approvedBooking: Booking? = null // Holds the active approved booking

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize views
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

        cancelButton.setOnClickListener {
            Log.d("BookingFragment", "Cancel button clicked. Booking ID: ${approvedBooking?.booking_id}")

            approvedBooking?.let { booking ->
                if (canCancelBooking(booking.date_formatted)) {
                    cancelBooking(booking.booking_id)
                } else {
                    showUnableToCancelPopup()
                }
            } ?: Toast.makeText(requireContext(), "No active booking to cancel", Toast.LENGTH_SHORT).show()
        }

        // Fetch and display booking history for the current user
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
                                        status = bookingHistory.status
                                    )
                                }

                                approvedBooking = bookings.find { it.status == "Approved" }

                                CoroutineScope(Dispatchers.Main).launch {
                                    if (approvedBooking != null) {
                                        tvActiveService.text = approvedBooking!!.service_type
                                        tvDate.text = approvedBooking!!.date_formatted
                                        tvTime.text = formatTimeTo12Hour(approvedBooking!!.time)
                                        activeServiceStatus.text = approvedBooking!!.status

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

                                    val adapter = BookingAdapter(requireContext(), bookings) { booking -> }
                                    bookingHistoryList.adapter = adapter
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
    private fun formatTimeTo12Hour(time: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val date = inputFormat.parse(time ?: "00:00:00")
            outputFormat.format(date ?: "").uppercase(Locale.getDefault())
        } catch (e: Exception) {
            "Not Available"
        }
    }


    private fun canCancelBooking(bookingDate: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) // Example: March 30, 2025
            val bookingTime = dateFormat.parse(bookingDate)
            val currentTime = Date()

            if (bookingTime != null) {
                val diff = currentTime.time - bookingTime.time
                val hoursDifference = diff / (1000 * 60 * 60)
                return hoursDifference < 24
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun showUnableToCancelPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Unable to Cancel")
            .setMessage("You can only cancel a booking within 24 hours after approval.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun getCurrentUserId(): String {
        val sessionManagement = SessionManagement(requireContext())
        return sessionManagement.getUserId() ?: ""
    }
}
