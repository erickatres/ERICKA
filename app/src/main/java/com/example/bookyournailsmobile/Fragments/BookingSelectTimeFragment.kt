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
    private lateinit var binding: BookingSelectTimeBinding // ViewBinding for the layout
    private var selectedTime: String? = null // To store the selected time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the service type from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        Log.d("BookingSelectTimeFragment", "Service Type: $serviceType")
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
            handleTimeButtonClick(binding.timeButton1, "9:00 AM")
        }
        binding.timeButton2.setOnClickListener {
            handleTimeButtonClick(binding.timeButton2, "1:00 PM")
        }
        binding.timeButton3.setOnClickListener {
            handleTimeButtonClick(binding.timeButton3, "4:00 PM")
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
            "9:00 AM" -> button.setImageResource(R.drawable.time_selected_9am)
            "1:00 PM" -> button.setImageResource(R.drawable.time_selected_1pm)
            "4:00 PM" -> button.setImageResource(R.drawable.time_selected_4pm)
        }

        // Store the selected time
        selectedTime = time

        // Enable the continue button
        binding.continueBooking.isEnabled = true
        binding.continueBooking.background = resources.getDrawable(R.drawable.continue_filled, null)
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

            // Navigate to BookingAttachImageFragment
            navigateToBookingAttachImageFragment()
        } else {
            Log.e("BookingSelectTimeFragment", "No time selected")
        }
    }

    private fun navigateToBookingAttachImageFragment() {
        // Create a new instance of BookingAttachImageFragment
        val bookingAttachImageFragment = BookingAttachImageFragment.newInstance(
            serviceType = serviceType ?: "Unknown",
            selectedTime = selectedTime ?: "Unknown"
        )

        // Replace the current fragment with BookingAttachImageFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, bookingAttachImageFragment)
            .addToBackStack(null) // Add the transaction to the back stack
            .commit()
    }

    companion object {
        /**
         * Create a new instance of BookingSelectTimeFragment with the service type.
         *
         * @param serviceType The type of service (e.g., "Regular" or "Gel Polish").
         * @return A new instance of BookingSelectTimeFragment.
         */
        @JvmStatic
        fun newInstance(serviceType: String) =
            BookingSelectTimeFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                }
            }
    }
}