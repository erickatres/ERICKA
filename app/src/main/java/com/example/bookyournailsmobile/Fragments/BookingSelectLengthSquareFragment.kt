package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.databinding.BookingSelectLengthSquareBinding

class BookingSelectLengthSquareFragment : Fragment() {
    private var _binding: BookingSelectLengthSquareBinding? = null
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
        _binding = BookingSelectLengthSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = serviceType ?: "Soft Gel Extension"
        binding.textView16.text = "Choose Length"

        // Set click listeners for selecting length
        binding.squareShape1.setOnClickListener { selectLength("Small") }
        binding.squareShape2.setOnClickListener { selectLength("Medium") }
        binding.squareShape3.setOnClickListener { selectLength("Long") }

        // Disable continue button initially
        binding.squareContinueBooking.isEnabled = false

        // Navigate to BookingAttachImageFragment when continue button is clicked
        binding.squareContinueBooking.setOnClickListener {
            val fragment = BookingAttachImageFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                    putString("SELECTED_TIME", selectedTime)
                    putString("SERVICE_PRICE", servicePrice)
                    putString("SELECTED_SHAPE", selectedShape)
                    putString("SELECTED_LENGTH", selectedLength) // Pass selected length
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace with actual container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }
    }

    private fun selectLength(length: String) {
        selectedLength = length

        // Reset all buttons to default unselected state
        binding.squareShape1.setImageResource(R.drawable.small_square_unfilled)
        binding.squareShape2.setImageResource(R.drawable.medium_square_unfilled)
        binding.squareShape3.setImageResource(R.drawable.long_square_unfilled)

        // Apply selected state to the clicked button
        when (length) {
            "Small" -> binding.squareShape1.setImageResource(R.drawable.small_square_filled)
            "Medium" -> binding.squareShape2.setImageResource(R.drawable.medium_square_filled)
            "Long" -> binding.squareShape3.setImageResource(R.drawable.long_square_filled)
        }

        // Enable continue button after selection
        binding.squareContinueBooking.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String, selectedTime: String, servicePrice: String, selectedShape: String, selectedLength: String) =
            BookingSelectLengthSquareFragment().apply {
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
