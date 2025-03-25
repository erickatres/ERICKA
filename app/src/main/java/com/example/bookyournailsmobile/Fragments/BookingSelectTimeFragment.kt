package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        // Initialize all buttons to unselected state
        initializeTimeButtons()

        // Set click listeners
        binding.timeButton1.setOnClickListener { handleTimeButtonClick(binding.timeButton1, "9:00:00 AM") }
        binding.timeButton2.setOnClickListener { handleTimeButtonClick(binding.timeButton2, "13:00:00 PM") }
        binding.timeButton3.setOnClickListener { handleTimeButtonClick(binding.timeButton3, "16:00:00 PM") }

        binding.continueBooking.setOnClickListener { handleContinueButtonClick() }
        binding.continueBooking.isEnabled = false
    }

    private fun initializeTimeButtons() {
        binding.timeButton1.setImageResource(R.drawable.time_unselected_9am)
        binding.timeButton2.setImageResource(R.drawable.time_unselected_1pm)
        binding.timeButton3.setImageResource(R.drawable.time_unselected_4pm)

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
        apiService.checkTimeAvailability(date, time).enqueue(
            object : Callback<ApiService.TimeAvailabilityResponse> {
                // 1. Implement onResponse
                override fun onResponse(
                    call: Call<ApiService.TimeAvailabilityResponse>,
                    response: Response<ApiService.TimeAvailabilityResponse>
                ) {
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

                // 2. Implement onFailure (this was missing)
                override fun onFailure(
                    call: Call<ApiService.TimeAvailabilityResponse>,
                    t: Throwable
                ) {
                    updateTimeButtonState(time, false)
                    Log.e("API", "Network call failed", t)
                    showToast("Network error. Please check your connection.")
                }
            }
        )
    }

    private fun updateTimeButtonState(time: String, isAvailable: Boolean) {
        val (button, selectedRes, unselectedRes, unavailableRes) = when (time) {
            "9:00:00 AM" -> Quadruple(
                binding.timeButton1,
                R.drawable.time_selected_9am,
                R.drawable.time_unselected_9am,
                R.drawable.ic_time_unavailable_9am
            )
            "13:00:00 PM" -> Quadruple(
                binding.timeButton2,
                R.drawable.time_selected_1pm,
                R.drawable.time_unselected_1pm,
                R.drawable.ic_time_unavailable_1pm
            )
            "16:00:00 PM" -> Quadruple(
                binding.timeButton3,
                R.drawable.time_selected_4pm,
                R.drawable.time_unselected_4pm,
                R.drawable.ic_time_unavailable_4pm
            )
            else -> return
        }

        button.isEnabled = isAvailable
        button.setImageResource(
            if (isAvailable) unselectedRes else unavailableRes
        )
        button.alpha = if (isAvailable) 1f else 0.6f
    }

    private fun handleTimeButtonClick(button: ImageView, time: String) {
        resetTimeButtons()

        when (time) {
            "9:00:00 AM" -> button.setImageResource(R.drawable.time_selected_9am)
            "13:00:00 PM" -> button.setImageResource(R.drawable.time_selected_1pm)
            "16:00:00 PM" -> button.setImageResource(R.drawable.time_selected_4pm)
        }

        selectedTime = time
        binding.continueBooking.isEnabled = true
        binding.continueBooking.setBackgroundResource(R.drawable.continue_filled)
    }

    private fun resetTimeButtons() {
        availableTimes.forEach { time ->
            when (time) {
                "9:00:00 AM" -> if (binding.timeButton1.isEnabled) {
                    binding.timeButton1.setImageResource(R.drawable.time_unselected_9am)
                }
                "13:00:00 PM" -> if (binding.timeButton2.isEnabled) {
                    binding.timeButton2.setImageResource(R.drawable.time_unselected_1pm)
                }
                "16:00:00 PM" -> if (binding.timeButton3.isEnabled) {
                    binding.timeButton3.setImageResource(R.drawable.time_unselected_4pm)
                }
            }
        }
    }

    private fun handleContinueButtonClick() {
        selectedTime?.let {
            navigateToBookingAttachImageFragment()
        } ?: showToast("Please select an available time")
    }

    private fun navigateToBookingAttachImageFragment() {
        val time = selectedTime ?: return
        BookingAttachImageFragment.newInstance(
            serviceType = serviceType ?: "Unknown",
            selectedTime = time
        ).apply {
            arguments?.putString("SELECTED_DATE", selectedDate)
        }.also { fragment ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
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

    private data class Quadruple<T>(
        val first: T,
        val second: Int,
        val third: Int,
        val fourth: Int
    )
}

// Add to your ApiService interface:
data class TimeAvailabilityResponse(
    val isAvailable: Boolean,
    val message: String? = null
)