package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectLengthAlmondBinding

class BookingSelectLengthAlmondFragment : Fragment() {
    private var _binding: BookingSelectLengthAlmondBinding? = null
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
        _binding = BookingSelectLengthAlmondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.almondShape1.setOnClickListener { selectLength("Small", R.drawable.small_almond_filled) }
        binding.almondShape2.setOnClickListener { selectLength("Medium", R.drawable.medium_almond_filled) }
        binding.almondShape3.setOnClickListener { selectLength("Long", R.drawable.long_almond_filled) }

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

    private fun selectLength(length: String, drawableId: Int) {
        selectedLength = length
        binding.continueBooking.isEnabled = true

        binding.almondShape1.setImageResource(R.drawable.small_almond_unfilled)
        binding.almondShape2.setImageResource(R.drawable.medium_almond_unfilled)
        binding.almondShape3.setImageResource(R.drawable.long_almond_unfilled)

        when (length) {
            "Small" -> binding.almondShape1.setImageResource(R.drawable.small_almond_filled)
            "Medium" -> binding.almondShape2.setImageResource(R.drawable.medium_almond_filled)
            "Long" -> binding.almondShape3.setImageResource(R.drawable.long_almond_filled)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String, selectedTime: String, servicePrice: String, selectedShape: String, selectedLength: String) =
            BookingSelectLengthAlmondFragment().apply {
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
