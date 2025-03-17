package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    private lateinit var appointment_backBTN: FrameLayout
    private lateinit var continue_button: Button
    private lateinit var textViewSD: TextView // TextView to display the selected date
    private var serviceType: String? = null // To store the service type (Regular or Gel Polish)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Retrieve the service type from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        Log.d("AppointmentFragment", "Service Type: $serviceType")

        // Initialize the back button
        appointment_backBTN = view.findViewById(R.id.appointment_backBTN)

        // Initialize the continue button
        continue_button = view.findViewById(R.id.btn_continue)
        continue_button.visibility = View.GONE // Hide the button initially

        // Initialize the TextView for displaying the selected date
        textViewSD = view.findViewById(R.id.textViewSD)

        // Set an OnClickListener on the back button
        appointment_backBTN.setOnClickListener {
            // Navigate back to the previous fragment
            navigateBackToPreviousFragment()
        }

        // Set an OnClickListener on the TextView to allow reselecting the date
        textViewSD.setOnClickListener {
            showMaterialDatePicker(textViewSD)
        }

        // Show MaterialDatePicker when AppointmentFragment is opened
        showMaterialDatePicker(textViewSD)

        // Set an OnClickListener on the continue button
        continue_button.setOnClickListener {
            // Navigate to BookingSelectTimeFragment with the selected service type
            navigateToBookingSelectTimeFragment(serviceType ?: "Unknown")
        }

        return view
    }

    private fun navigateBackToPreviousFragment() {
        // Use FragmentManager to navigate back to the previous fragment
        parentFragmentManager.popBackStack()
    }

    private fun showMaterialDatePicker(textView: TextView) {
        // Get the current date in UTC milliseconds
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // Build constraints to disallow dates before today
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(today) // Set the start date to today
            .setValidator(DateValidatorPointForward.from(today)) // Ensure no dates before today can be selected

        // Build MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Appointment Date") // Optional: Add a title
            .setSelection(today) // Default to today's date
            .setCalendarConstraints(constraintsBuilder.build()) // Apply constraints
            .build()

        // Show the picker
        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")

        // Adjust size AFTER the dialog is shown (optional)
        datePicker.dialog?.setOnShowListener {
            val window = datePicker.dialog?.window
            window?.setLayout(900, 500) // Adjust width and height (in pixels)
        }

        // Handle the selected date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = formatter.format(selectedDate)
            textView.text = formattedDate // Show selected date in TextView

            // Show the continue button once a date is selected
            continue_button.visibility = View.VISIBLE
        }
    }

    private fun navigateToBookingSelectTimeFragment(serviceType: String) {
        // Create a new instance of BookingSelectTimeFragment
        val bookingSelectTimeFragment = BookingSelectTimeFragment.newInstance(serviceType)

        // Replace the current fragment with BookingSelectTimeFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, bookingSelectTimeFragment)
            .addToBackStack(null) // Add the transaction to the back stack
            .commit()
    }
}