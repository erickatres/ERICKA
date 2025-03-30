package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.Models.TimeAvailabilityResponse
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectTimeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingSelectTimeFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private lateinit var binding: BookingSelectTimeBinding
    private var selectedTime: String? = null
    private lateinit var apiService: ApiService
    private val availableTimes = listOf("9:00:00 AM", "13:00:00 PM", "16:00:00 PM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceType = it.getString("SERVICE_TYPE")
            selectedDate = it.getString("SELECTED_DATE")
        }

        try {
            apiService = RetrofitClient.create(requireContext())
        } catch (e: Exception) {
            Log.e("RetrofitError", "Failed to create API service", e)
            showToast("Failed to initialize booking service")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookingSelectTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        checkTimeSlotsAvailability()
    }

    private fun setupUI() {
        initializeTimeButtons()

        binding.timeButton1.setOnClickListener { handleTimeButtonClick(binding.timeButton1, "9:00:00 AM") }
        binding.timeButton2.setOnClickListener { handleTimeButtonClick(binding.timeButton2, "13:00:00 PM") }
        binding.timeButton3.setOnClickListener { handleTimeButtonClick(binding.timeButton3, "16:00:00 PM") }

        binding.continueBooking.setOnClickListener { handleContinueButtonClick() }
        binding.continueBooking.isEnabled = false
    }

    private fun initializeTimeButtons() {
        // Default to available state
        binding.timeButton1.setImageResource(R.drawable.ic_time_available_9am)
        binding.timeButton2.setImageResource(R.drawable.ic_time_available_1pm)
        binding.timeButton3.setImageResource(R.drawable.ic_time_available_4pm)

        binding.timeButton1.isEnabled = false
        binding.timeButton2.isEnabled = false
        binding.timeButton3.isEnabled = false
    }

    private fun checkTimeSlotsAvailability() {
        selectedDate?.let { date ->
            availableTimes.forEach { time ->
                checkTimeAvailability(date, time)
            }
        } ?: showToast("No date selected")
    }

    private fun checkTimeAvailability(date: String, time: String) {
        val request = ApiService.TimeAvailabilityRequest(date, time)
        apiService.checkTimeAvailability(request).enqueue(object : Callback<TimeAvailabilityResponse> {
            override fun onResponse(call: Call<TimeAvailabilityResponse>, response: Response<TimeAvailabilityResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { availabilityResponse ->
                        updateTimeButtonState(time, availabilityResponse.isAvailable)
                    } ?: run {
                        updateTimeButtonState(time, false)
                        Log.e("API", "Response body is null")
                    }
                } else {
                    updateTimeButtonState(time, false)
                    Log.e("API", "Server responded with error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TimeAvailabilityResponse>, t: Throwable) {
                updateTimeButtonState(time, false)
                Log.e("API", "Network call failed", t)
                showToast("Network error. Please check your connection.")
            }
        })
    }

    private fun updateTimeButtonState(time: String, isAvailable: Boolean) {
        val button = when (time) {
            "9:00:00 AM" -> binding.timeButton1
            "13:00:00 PM" -> binding.timeButton2
            "16:00:00 PM" -> binding.timeButton3
            else -> return
        }

        if (isAvailable) {
            button.isEnabled = true
            button.setImageResource(getAvailableImage(time))
        } else {
            button.isEnabled = false
            button.setImageResource(getNotAvailableImage(time))
            button.alpha = 0.6f
        }
    }

    private fun handleTimeButtonClick(button: ImageView, time: String) {
        resetTimeButtons()

        when (time) {
            "9:00:00 AM" -> binding.timeButton1.setImageResource(R.drawable.ic_time_selected_9am)
            "13:00:00 PM" -> binding.timeButton2.setImageResource(R.drawable.ic_time_selected_1pm)
            "16:00:00 PM" -> binding.timeButton3.setImageResource(R.drawable.ic_time_selected_4pm)
        }

        selectedTime = time
        binding.continueBooking.isEnabled = true
        binding.continueBooking.setBackgroundResource(R.drawable.continue_filled)
    }

    private fun resetTimeButtons() {
        // Reset all buttons to their "available" state unless they are not available
        if (binding.timeButton1.isEnabled) {
            binding.timeButton1.setImageResource(R.drawable.ic_time_available_9am)
        }
        if (binding.timeButton2.isEnabled) {
            binding.timeButton2.setImageResource(R.drawable.ic_time_available_1pm)
        }
        if (binding.timeButton3.isEnabled) {
            binding.timeButton3.setImageResource(R.drawable.ic_time_available_4pm)
        }
    }

    private fun getAvailableImage(time: String): Int {
        return when (time) {
            "9:00:00 AM" -> R.drawable.ic_time_available_9am
            "13:00:00 PM" -> R.drawable.ic_time_available_1pm
            "16:00:00 PM" -> R.drawable.ic_time_available_4pm
            else -> R.drawable.ic_time_available_9am
        }
    }

    private fun getNotAvailableImage(time: String): Int {
        return when (time) {
            "9:00:00 AM" -> R.drawable.ic_time_notavailable_9am
            "13:00:00 PM" -> R.drawable.ic_time_notavailable_1pm
            "16:00:00 PM" -> R.drawable.ic_time_notavailable_4pm
            else -> R.drawable.ic_time_notavailable_9am
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisibility(false) // Hide bottom nav
    }


    private fun handleContinueButtonClick() {
        selectedTime?.let {
            when (serviceType) {
                "Removal" -> navigateToSummaryRegularPlainFragment()
                "Soft Gel X" -> navigateToBookingSelectShapeFragment() // Navigate to shape selection
                else -> navigateToBookingAttachImageFragment()
            }
        } ?: showToast("Please select an available time")
    }


    private fun getServicePrice(serviceType: String?): String {
        return when (serviceType) {
            "Regular Plain" -> "₱450"
            "Gel Polish" -> "₱650"
            "Removal" -> "₱250"
            "Soft Gel X" -> "₱950"
            else -> "null"
        }
    }
    private fun navigateToBookingSelectShapeFragment() {
        val time = selectedTime ?: return
        val price = getServicePrice(serviceType) // Get the service price

        val fragment = BookingSelectShapeFragment()
        fragment.arguments = Bundle().apply {
            putString("SELECTED_DATE", selectedDate)
            putString("SERVICE_TYPE", serviceType)
            putString("SELECTED_TIME", time)
            putString("SERVICE_PRICE", price)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun navigateToSummaryRegularPlainFragment() {
        val time = selectedTime ?: return
        val price = getServicePrice(serviceType) // Get the service price

        val fragment = SummaryRegularPlainFragment() // Replace with the actual constructor if needed
        fragment.arguments = Bundle().apply {
            putString("SELECTED_DATE", selectedDate)
            putString("SERVICE_TYPE", serviceType)
            putString("SELECTED_TIME", time)
            putString("SERVICE_PRICE", price)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }



    private fun navigateToBookingAttachImageFragment() {
        val time = selectedTime ?: return
        val price = getServicePrice(serviceType) // Get the service price

        val fragment = BookingAttachImageFragment.newInstance(serviceType ?: "Unknown", time)
        fragment.arguments?.putString("SELECTED_DATE", selectedDate)
        fragment.arguments?.putString("SERVICE_PRICE", price) // Pass service price to the next fragment

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    companion object {
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
