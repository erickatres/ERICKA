package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectLengthRoundBinding

class BookingSelectLengthRoundFragment : Fragment() {
    private var _binding: BookingSelectLengthRoundBinding? = null
    private val binding get() = _binding!!

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var selectedShape: String? = null
    private var selectedLength: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceType = it.getString("SERVICE_TYPE")
            selectedDate = it.getString("SELECTED_DATE")
            selectedTime = it.getString("SELECTED_TIME")
            servicePrice = it.getString("SERVICE_PRICE")
            selectedShape = it.getString("SELECTED_SHAPE")
            selectedLength = it.getString("SELECTED_LENGTH")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookingSelectLengthRoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = serviceType ?: "Soft Gel Extension"
        binding.textView14.text = "Choose Length"

        binding.roundShape1.setOnClickListener { selectLength("Small") }
        binding.roundShape2.setOnClickListener { selectLength("Medium") }
        binding.roundShape3.setOnClickListener { selectLength("Long") }

        binding.continueBooking.isEnabled = false

        binding.continueBooking.setOnClickListener {
            val fragment = BookingAttachImageFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                    putString("SELECTED_TIME", selectedTime)
                    putString("SERVICE_PRICE", servicePrice)
                    putString("SELECTED_SHAPE", selectedShape)
                    putString("SELECTED_LENGTH", selectedLength)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun selectLength(length: String) {
        selectedLength = length

        binding.roundShape1.setImageResource(R.drawable.small_round_unfilled)
        binding.roundShape2.setImageResource(R.drawable.medium_round_unfilled)
        binding.roundShape3.setImageResource(R.drawable.long_round_unfilled)

        when (length) {
            "Small" -> binding.roundShape1.setImageResource(R.drawable.small_round_filled)
            "Medium" -> binding.roundShape2.setImageResource(R.drawable.medium_round_filled)
            "Long" -> binding.roundShape3.setImageResource(R.drawable.long_round_filled)
        }

        binding.continueBooking.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String, selectedTime: String, servicePrice: String, selectedShape: String, selectedLength: String) =
            BookingSelectLengthRoundFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                    putString("SELECTED_TIME", selectedTime)
                    putString("SERVICE_PRICE", servicePrice)
                    putString("SELECTED_SHAPE", selectedShape)
                    putString("SELECTED_LENGTH", selectedLength)
                }
            }
    }
}
