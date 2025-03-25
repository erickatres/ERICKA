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
import com.example.bookyournailsmobile.Class.DateValidatorNoPastAndNoSundays
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

    private var backendFormattedDate: String? = null // Store the date in "YYYY-MM-DD" format for backend

    private fun showMaterialDatePicker(textView: TextView) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // Calculate the maximum date (5 days ahead)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today
        calendar.add(Calendar.DAY_OF_MONTH, 5)
        val maxDate = calendar.timeInMillis

        // Build constraints: start from today, end after 5 days, exclude past dates & Sundays
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(today) // Start from today (prevents past dates)
            .setEnd(maxDate) // Allow only the next 5 days
            .setValidator(DateValidatorNoPastAndNoSundays()) // Custom validator

        // Build MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Appointment Date")
            .setSelection(today) // Default to today's date
            .setCalendarConstraints(constraintsBuilder.build()) // Apply constraints
            .build()

        // Show the picker
        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")

        // Handle the selected date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)

            // Format for display: "March 29, 2025"
            val displayFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val formattedDisplayDate = displayFormatter.format(selectedDate)

            // Format for backend: "2025-03-29"
            val backendFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            backendFormattedDate = backendFormatter.format(selectedDate)

            textView.text = formattedDisplayDate // Show user-friendly format in TextView
            continue_button.visibility = View.VISIBLE // Show continue button
        }
    }

    // Example: Send the backend format when navigating
    private fun navigateToBookingSelectTimeFragment(serviceType: String) {
        val selectedDate = backendFormattedDate ?: return // Ensure a date is selected

        val bookingSelectTimeFragment = BookingSelectTimeFragment.newInstance(serviceType, selectedDate)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, bookingSelectTimeFragment)
            .addToBackStack(null)
            .commit()
    }
}
