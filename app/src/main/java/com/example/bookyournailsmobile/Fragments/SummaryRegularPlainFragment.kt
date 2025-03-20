package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.NetUtils.BookingRequest
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryRegularPlainFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var referenceImageUri: String? = null

    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var totalServicePrice: TextView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedDate = arguments?.getString("SELECTED_DATE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        servicePrice = arguments?.getString("SERVICE_PRICE")
        referenceImageUri = arguments?.getString("REFERENCE_IMAGE_URI")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.summary_regular_plain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvService = view.findViewById(R.id.tvService)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tvTime)
        tvMobile = view.findViewById(R.id.tvMobile)
        tvServicePrice = view.findViewById(R.id.tvservicePrice)
        totalServicePrice = view.findViewById(R.id.total_service_price)
        btnConfirm = view.findViewById(R.id.btnConfirm)

        val user = requireContext().getUserFromPreferences()
        user?.let {
            tvMobile.text = it.phone
        }

        tvService.text = serviceType ?: "N/A"
        tvDate.text = selectedDate ?: "N/A"
        tvTime.text = selectedTime ?: "N/A"
        totalServicePrice.text = servicePrice ?: "N/A"
        tvServicePrice.text = servicePrice ?: "N/A"

        btnConfirm.setOnClickListener {
            user?.let {
                uploadBookingToServer(it)
            } ?: run {
                Toast.makeText(requireContext(), "User not found. Please log in.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(
            serviceType: String,
            selectedDate: String,
            selectedTime: String,
            servicePrice: String,
            imageUri: String
        ) = SummaryRegularPlainFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType)
                putString("SELECTED_DATE", selectedDate)
                putString("SELECTED_TIME", selectedTime)
                putString("SERVICE_PRICE", servicePrice)
                putString("IMAGE_URI", imageUri)
            }
        }
    }


    private fun uploadBookingToServer(user: User) {
        val bookingRequest = BookingRequest(
            user_id = user.getId() ?: "",
            service_type = serviceType ?: "",
            status = "Pending",
            reference_img = referenceImageUri ?: "",
            price = servicePrice ?: "",
            date = selectedDate ?: "",
            time = selectedTime ?: ""
        )

        val call = RetrofitClient.instance.createBooking(bookingRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Booking failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
