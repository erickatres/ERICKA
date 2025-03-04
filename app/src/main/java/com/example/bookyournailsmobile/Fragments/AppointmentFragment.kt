package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    private lateinit var appointment_backBTN: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Initialize the back button correctly
        appointment_backBTN = view.findViewById(R.id.appointment_backBTN)

        // Set an OnClickListener on the back button
        appointment_backBTN.setOnClickListener {
            // Navigate back to BookingFragment
            navigateBackToBookingFragment()
        }

        val textViewSD = view.findViewById<TextView>(R.id.textViewSD) // Date display

        // Show MaterialDatePicker when AppointmentFragment is opened
        showMaterialDatePicker(textViewSD)

        return view
    }

    private fun navigateBackToBookingFragment() {
        // Use FragmentManager to navigate back to BookingFragment
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BookingFragment())
            .addToBackStack(null) // Optional: Add to back stack
            .commit()
    }

    private fun showMaterialDatePicker(textView: TextView) {
        // Build constraints for allowed date ranges (optional)
        val constraintsBuilder = CalendarConstraints.Builder()

        // Build MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("") // Remove "Select a Date"
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default to today's date
            .setCalendarConstraints(constraintsBuilder.build())
            // Custom theme (optional)
            .build()

        // Show the picker
        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")

        // Adjust size AFTER the dialog is shown
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
        }
    }
}