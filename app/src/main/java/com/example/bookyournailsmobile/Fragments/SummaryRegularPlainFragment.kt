package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Request.BookingRequest

class SummaryRegularPlainFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var referenceImageUri: String? = null

    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var tvServicePriceDetails: TextView
    private lateinit var totalServicePrice: TextView
    private lateinit var btnConfirm: Button

    // Retrofit API service
    private val apiService = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the service type, selected date, selected time, and service price from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedDate = arguments?.getString("SELECTED_DATE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        servicePrice = arguments?.getString("SERVICE_PRICE")
        referenceImageUri = arguments?.getString("REFERENCE_IMAGE_URI")
        Log.d(
            "SummaryRegularPlainFragment",
            "Service Type: $serviceType, Selected Date: $selectedDate, Selected Time: $selectedTime, Service Price: $servicePrice, Reference Image URI: $referenceImageUri"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_regular_plain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextViews
        tvService = view.findViewById(R.id.tvService)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tvTime)
        tvMobile = view.findViewById(R.id.tvMobile)
        tvServicePrice = view.findViewById(R.id.tvservicePrice)
        tvServicePriceDetails = view.findViewById(R.id.service_price_details)
        totalServicePrice = view.findViewById(R.id.total_service_price)
        btnConfirm = view.findViewById(R.id.btnConfirm)

        // Fetch the current user from shared preferences or session
        val user = requireContext().getUserFromPreferences()
        user?.let {
            tvMobile.text = it.phone
        }

        // Set data to TextViews
        tvService.text = serviceType ?: "N/A"
        tvServicePriceDetails.text = serviceType ?: "N/A"
        tvDate.text = selectedDate ?: "N/A"
        tvTime.text = selectedTime ?: "N/A"
        totalServicePrice.text = servicePrice ?: "N/A"
        tvServicePrice.text = servicePrice ?: "N/A"

        // Set click listener for the confirm button
        btnConfirm.setOnClickListener {
            uploadBookingToServer(user)
        }
    }

    private fun uploadBookingToServer(user: User?) {
        // Ensure all fields are not null
        val userId = user?.getId() ?: run {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val serviceType = serviceType ?: run {
            Toast.makeText(requireContext(), "Service type is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedDate = selectedDate ?: run {
            Toast.makeText(requireContext(), "Date is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedTime = selectedTime ?: run {
            Toast.makeText(requireContext(), "Time is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val servicePrice = servicePrice ?: run {
            Toast.makeText(requireContext(), "Price is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val referenceImageUri = referenceImageUri ?: run {
            Toast.makeText(requireContext(), "Reference image is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a BookingRequest object
        val bookingRequest = com.example.bookyournailsmobile.NetUtils.BookingRequest(
            user_id = userId,
            service_type = serviceType,
            status = "Pending", // Default status
            reference_img = referenceImageUri,
            price = servicePrice,
            date = selectedDate,
            time = selectedTime
        )

        // Make an API call to upload the booking data as JSON
        apiService.createBooking(bookingRequest).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Booking created successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("SummaryRegularPlainFragment", "Booking uploaded successfully")
                } else {
                    Toast.makeText(requireContext(), "Failed to create booking. Please try again.", Toast.LENGTH_SHORT).show()
                    Log.e("SummaryRegularPlainFragment", "Failed to upload booking: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                Log.e("SummaryRegularPlainFragment", "Network error: ${t.message}")
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(
            serviceType: String,
            selectedDate: String,
            selectedTime: String,
            servicePrice: String,
            referenceImageUri: String
        ) = SummaryRegularPlainFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType)
                putString("SELECTED_DATE", selectedDate)
                putString("SELECTED_TIME", selectedTime)
                putString("SERVICE_PRICE", servicePrice)
                putString("REFERENCE_IMAGE_URI", referenceImageUri)
            }
        }
    }
}