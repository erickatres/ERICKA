package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectColorBinding

class BookingSelectColorFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedTime: String? = null
    private var selectedColor: String? = null // To store the selected color
    private lateinit var binding: BookingSelectColorBinding // ViewBinding for the layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the service type and selected time from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        Log.d("BookingSelectColorFragment", "Service Type: $serviceType, Selected Time: $selectedTime")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        binding = BookingSelectColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for all color buttons
        setColorButtonClickListeners()

        // Set click listener for the continue button
        binding.continueBooking.setOnClickListener {
            handleContinueButtonClick()
        }
    }

    private fun setColorButtonClickListeners() {
        // Set click listeners for all color buttons
        binding.color01.setOnClickListener { handleColorButtonClick(it, "#01") }
        binding.color02.setOnClickListener { handleColorButtonClick(it, "#02") }
        binding.color03.setOnClickListener { handleColorButtonClick(it, "#03") }
        binding.color04.setOnClickListener { handleColorButtonClick(it, "#04") }
        binding.color05.setOnClickListener { handleColorButtonClick(it, "#05") }
        binding.color06.setOnClickListener { handleColorButtonClick(it, "#06") }
        binding.color07.setOnClickListener { handleColorButtonClick(it, "#07") }
        binding.color08.setOnClickListener { handleColorButtonClick(it, "#08") }
        binding.color09.setOnClickListener { handleColorButtonClick(it, "#09") }
        binding.color10.setOnClickListener { handleColorButtonClick(it, "#10") }
        binding.color11.setOnClickListener { handleColorButtonClick(it, "#11") }
        binding.color12.setOnClickListener { handleColorButtonClick(it, "#12") }
        binding.color13.setOnClickListener { handleColorButtonClick(it, "#13") }
        binding.color14.setOnClickListener { handleColorButtonClick(it, "#14") }
        binding.color15.setOnClickListener { handleColorButtonClick(it, "#15") }
        binding.color16.setOnClickListener { handleColorButtonClick(it, "#16") }
        binding.color17.setOnClickListener { handleColorButtonClick(it, "#17") }
        binding.color18.setOnClickListener { handleColorButtonClick(it, "#18") }
        binding.color19.setOnClickListener { handleColorButtonClick(it, "#19") }
        binding.color20.setOnClickListener { handleColorButtonClick(it, "#20") }
    }

    private fun handleColorButtonClick(button: View, color: String) {
        // Reset all color buttons to their unselected state
        resetColorButtons()

        // Set the selected color button to its selected state
        button.isSelected = true

        // Store the selected color
        selectedColor = color

        // Enable the continue button
        binding.continueBooking.isEnabled = true
        binding.continueBooking.background = resources.getDrawable(R.drawable.booking_continue_button, null)
    }

    private fun resetColorButtons() {
        // Reset all color buttons to their unselected state
        binding.color01.isSelected = false
        binding.color02.isSelected = false
        binding.color03.isSelected = false
        binding.color04.isSelected = false
        binding.color05.isSelected = false
        binding.color06.isSelected = false
        binding.color07.isSelected = false
        binding.color08.isSelected = false
        binding.color09.isSelected = false
        binding.color10.isSelected = false
        binding.color11.isSelected = false
        binding.color12.isSelected = false
        binding.color13.isSelected = false
        binding.color14.isSelected = false
        binding.color15.isSelected = false
        binding.color16.isSelected = false
        binding.color17.isSelected = false
        binding.color18.isSelected = false
        binding.color19.isSelected = false
        binding.color20.isSelected = false
    }

    private fun handleContinueButtonClick() {
        // Handle the continue button click
        if (selectedColor != null) {
            Log.d(
                "BookingSelectColorFragment",
                "Selected Color: $selectedColor for $serviceType at $selectedTime"
            )

            // Navigate to the next fragment (e.g., BookingConfirmationFragment)
            // You can implement this logic based on your app's navigation flow
        } else {
            Log.e("BookingSelectColorFragment", "No color selected")
        }
    }

    companion object {
        /**
         * Create a new instance of BookingSelectColorFragment with the service type and selected time.
         *
         * @param serviceType The type of service (e.g., "Regular" or "Gel Polish").
         * @param selectedTime The selected time (e.g., "9:00 AM").
         * @return A new instance of BookingSelectColorFragment.
         */
        @JvmStatic
        fun newInstance(serviceType: String, selectedTime: String) =
            BookingSelectColorFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_TIME", selectedTime)
                }
            }
    }
}