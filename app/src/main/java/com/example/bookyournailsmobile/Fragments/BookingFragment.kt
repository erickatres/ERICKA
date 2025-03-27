package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
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
class BookingFragment : Fragment() {

    private lateinit var bookingHistoryList: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize the ListView
        bookingHistoryList = view.findViewById(R.id.booking_history_list)

        // Fetch and display booking history for the current user
        fetchAndDisplayBookingHistory()

        return view
    }

    private fun fetchAndDisplayBookingHistory() {
        // Get the current user ID
        val userId = getCurrentUserId()
        println("Current User ID: $userId") // Log the user ID

        if (userId.isEmpty()) {
            // Handle the case where the user ID is not available (e.g., user is not logged in)
            println("User ID is not available. Please log in.")
            return
        }

        // Use Retrofit to fetch booking history from the API
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
                            if (bookingHistoryResponse != null) {
                                // Log the response for debugging
                                println("Booking History Response: $bookingHistoryResponse")

                                // Map history to Booking
                                val bookings = bookingHistoryResponse.history.map { bookingHistory ->
                                    Booking(
                                        service_type = bookingHistory.service_type,
                                        date_formatted = bookingHistory.date_formatted,
                                        status = bookingHistory.status // Pass the status here
                                    )
                                }


                                // Log the mapped bookings for debugging
                                println("Mapped Bookings: $bookings")

                                // Update the UI on the main thread
                                CoroutineScope(Dispatchers.Main).launch {
                                    val adapter = BookingAdapter(requireContext(), bookings) { booking ->
                                        // Transition to ReviewFormFragment when rateButton is clicked
                                        val reviewFormFragment = ReviewFormFragment().apply {
                                            arguments = Bundle().apply {
                                                putString("service_type", booking.service_type)
                                                putString("service_date", booking.date_formatted)
                                                putString("status", booking.status)
                                            }
                                        }

                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.fragment_container, reviewFormFragment) // Ensure the correct container ID
                                            .addToBackStack(null) // Allow back navigation
                                            .commit()
                                    }
                                    bookingHistoryList.adapter = adapter
                                }


                            } else {
                                // Handle null response
                                println("Booking history response is null")
                            }
                        } else {
                            // Handle unsuccessful response
                            println("Failed to fetch booking history: ${response.message()}")
                            println("Response Code: ${response.code()}")
                            println("Error Body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ApiService.BookingHistoryResponse>, t: Throwable) {
                        // Handle API call failure
                        t.printStackTrace()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Retrieve the current user ID from SharedPreferences
    private fun getCurrentUserId(): String {
        val sessionManagement = SessionManagement(requireContext())
        val userId = sessionManagement.getUserId() ?: ""
        Log.d("BookingFragment", "Retrieved User ID: $userId")
        return userId
    }
}