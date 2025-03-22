package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectTimeBinding

class BookingSelectTimeFragment : Fragment() {

    private var serviceType: String? = null // To store the service type (Regular or Gel Polish)
    private var selectedDate: String? = null // To store the selected date from AppointmentFragment
    private lateinit var binding: BookingSelectTimeBinding // ViewBinding for the layout
    private var selectedTime: String? = null // To store the selected time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the service type and selected date from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedDate = arguments?.getString("SELECTED_DATE")
        Log.d("BookingSelectTimeFragment", "Service Type: $serviceType, Selected Date: $selectedDate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        binding = BookingSelectTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for time buttons
        binding.timeButton1.setOnClickListener {
            handleTimeButtonClick(binding.timeButton1, "9:00:00 AM")
        }
        binding.timeButton2.setOnClickListener {
            handleTimeButtonClick(binding.timeButton2, "13:00:00 PM")
        }
        binding.timeButton3.setOnClickListener {
            handleTimeButtonClick(binding.timeButton3, "16:00:00 PM")
        }

        // Set click listener for the continue button
        binding.continueBooking.setOnClickListener {
            handleContinueButtonClick()
        }
    }

    private fun handleTimeButtonClick(button: ImageView, time: String) {
        // Reset all time buttons to their unselected state
        resetTimeButtons()

        // Set the selected time button to its selected state
        when (time) {
            "9:00:00 AM" -> button.setImageResource(R.drawable.time_selected_9am)
            "13:00:00 PM" -> button.setImageResource(R.drawable.time_selected_1pm)
            "16:00:00 PM" -> button.setImageResource(R.drawable.time_selected_4pm)
        }

        // Store the selected time
        selectedTime = time

        // Enable the continue button and change its background to the filled state
        binding.continueBooking.isEnabled = true
        binding.continueBooking.setBackgroundResource(R.drawable.continue_filled) // Use setBackgroundResource instead
    }

    private fun resetTimeButtons() {
        // Reset all time buttons to their unselected state
        binding.timeButton1.setImageResource(R.drawable.time_unselected_9am)
        binding.timeButton2.setImageResource(R.drawable.time_unselected_1pm)
        binding.timeButton3.setImageResource(R.drawable.time_unselected_4pm)
    }

    private fun handleContinueButtonClick() {
        // Handle the continue button click
        if (selectedTime != null) {
            Log.d("BookingSelectTimeFragment", "Selected Time: $selectedTime for $serviceType")

            // Navigate to BookingAttachImageFragment with the service type, selected date, and selected time
            navigateToBookingAttachImageFragment()
        } else {
            Log.e("BookingSelectTimeFragment", "No time selected")
        }
    }

    private fun navigateToBookingAttachImageFragment() {
        // Ensure selectedTime is not null
        val time = selectedTime ?: "Unknown"

        // Create a new instance of BookingAttachImageFragment with service type and selected time
        val bookingAttachImageFragment = BookingAttachImageFragment.newInstance(
            serviceType = serviceType ?: "Unknown", // Pass serviceType
            selectedTime = time // Pass selectedTime
        )

        // Pass the selected date to BookingAttachImageFragment
        bookingAttachImageFragment.arguments?.putString("SELECTED_DATE", selectedDate)

        // Replace the current fragment with BookingAttachImageFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, bookingAttachImageFragment)
            .addToBackStack(null) // Add the transaction to the back stack
            .commit()
    }

    companion object {
        /**
         * Create a new instance of BookingSelectTimeFragment with the service type and selected date.
         *
         * @param serviceType The type of service (e.g., "Regular" or "Gel Polish").
         * @param selectedDate The selected date from AppointmentFragment.
         * @return A new instance of BookingSelectTimeFragment.
         */
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String) =
            BookingSelectTimeFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                }
            }
    }
}