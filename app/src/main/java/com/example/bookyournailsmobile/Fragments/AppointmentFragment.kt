package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.Class.DateValidatorNoPastAndNoSundays
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    private lateinit var appointment_backBTN: FrameLayout
    private lateinit var continue_button: Button
    private lateinit var textViewSD: TextView
    private var serviceType: String? = null
    private lateinit var apiService: ApiService
    private var backendFormattedDate: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize API service with context
        apiService = RetrofitClient.create(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Retrieve the service type from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        Log.d("AppointmentFragment", "Service Type: $serviceType")

        // Initialize views
        appointment_backBTN = view.findViewById(R.id.appointment_backBTN)
        continue_button = view.findViewById(R.id.btn_continue)
        continue_button.visibility = View.GONE
        textViewSD = view.findViewById(R.id.textViewSD)

        // Set click listeners
        appointment_backBTN.setOnClickListener { navigateBackToPreviousFragment() }
        textViewSD.setOnClickListener { showMaterialDatePicker(textViewSD) }
        continue_button.setOnClickListener {
            backendFormattedDate?.let { date ->
                checkDateAvailability(date)
            }
        }

        // Show date picker when fragment is opened
        showMaterialDatePicker(textViewSD)

        return view
    }

    private fun checkDateAvailability(date: String) {
        continue_button.isEnabled = false

        apiService.checkDateAvailability(date).enqueue(object : Callback<ApiService.DateAvailabilityResponse> {
            override fun onResponse(
                call: Call<ApiService.DateAvailabilityResponse>,
                response: Response<ApiService.DateAvailabilityResponse>
            ) {
                continue_button.isEnabled = true

                if (response.isSuccessful) {
                    val isFull = response.body()?.fullyBooked ?: false
                    if (isFull) {
                        showFullyBookedDialog()
                    } else {
                        navigateToBookingSelectTimeFragment(serviceType ?: "Unknown")
                    }
                } else {
                    showErrorToast("Error checking date availability")
                }
            }

            override fun onFailure(
                call: Call<ApiService.DateAvailabilityResponse>,
                t: Throwable
            ) {
                continue_button.isEnabled = true
                showErrorToast("Network error: ${t.message}")
            }
        })
    }

    private fun showFullyBookedDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Fully Booked")
            .setMessage("The selected date is fully booked. Please choose another date.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                showMaterialDatePicker(textViewSD)
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showMaterialDatePicker(textView: TextView) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val maxDate = getLastDayOfYear()

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorNoPastAndNoSundays())
            .setStart(today)
            .setEnd(maxDate)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a Date")
            .setSelection(today)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (dayOfWeek == Calendar.SUNDAY) {
                showSundayAlertDialog()
            } else {
                // Format for display
                val displayFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val formattedDisplayDate = displayFormatter.format(selectedDate)

                // Format for backend
                val backendFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                backendFormattedDate = backendFormatter.format(selectedDate)

                textView.text = formattedDisplayDate
                continue_button.visibility = View.VISIBLE
            }
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun showSundayAlertDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Closed on Sundays")
            .setMessage("We are closed on Sundays. Please select another date.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getLastDayOfYear(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentYear = cal.get(Calendar.YEAR)
        cal.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59)
        return cal.timeInMillis
    }

    private fun navigateToBookingSelectTimeFragment(serviceType: String) {
        val selectedDate = backendFormattedDate ?: return

        val bookingSelectTimeFragment = BookingSelectTimeFragment.newInstance(serviceType, selectedDate)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, bookingSelectTimeFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateBackToPreviousFragment() {
        parentFragmentManager.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisibility(false)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }
}