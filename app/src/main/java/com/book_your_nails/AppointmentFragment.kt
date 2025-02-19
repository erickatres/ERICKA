package com.book_your_nails

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)
        val textViewSD = view.findViewById<TextView>(R.id.textViewSD) // Date display

        // Show the custom DatePicker dialog when the fragment opens
        showCustomDatePickerDialog(textViewSD)

        return view
    }

    private fun showCustomDatePickerDialog(textView: TextView) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)

        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val continueButton = dialogView.findViewById<Button>(R.id.continueBtn)

        // 🔹 Find and remove the header (works on most Android versions)
        val headerId = resources.getIdentifier("date_picker_header", "id", "android")
        val headerView = datePicker.findViewById<View>(headerId)
        headerView?.visibility = View.GONE  // Hide it

        // Create and configure the dialog
        val dialog = android.app.AlertDialog.Builder(requireContext(), R.style.CustomMaterialDatePickerTheme) // Apply the custom theme here
            .setView(dialogView)
            .create()

        dialog.setCancelable(true) // Allow cancellation of the dialog

        continueButton.setOnClickListener {
            val selectedDate = getSelectedDate(datePicker)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            textView.text = formatter.format(selectedDate)
            dialog.dismiss() // Dismiss the dialog after selecting the date
        }

        dialog.show()
    }

    private fun getSelectedDate(datePicker: DatePicker): Date {
        val year = datePicker.year
        val month = datePicker.month
        val day = datePicker.dayOfMonth
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.time
    }
}
