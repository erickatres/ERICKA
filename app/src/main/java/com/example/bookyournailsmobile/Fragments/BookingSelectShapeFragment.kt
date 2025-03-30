package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R

class BookingSelectShapeFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var selectedShape: String? = null

    private lateinit var almondShape: ImageView
    private lateinit var roundShape: ImageView
    private lateinit var squareShape: ImageView
    private lateinit var continueBooking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceType = it.getString("SERVICE_TYPE")
            selectedDate = it.getString("SELECTED_DATE")
            selectedTime = it.getString("SELECTED_TIME")
            servicePrice = it.getString("SERVICE_PRICE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_booking_select_shape, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        almondShape = view.findViewById(R.id.almondShape)
        roundShape = view.findViewById(R.id.roundShape)
        squareShape = view.findViewById(R.id.squareShape)
        continueBooking = view.findViewById(R.id.continueBooking)

        // Disable continue button initially
        continueBooking.isEnabled = false
        continueBooking.setBackgroundResource(R.drawable.continue_unfilled)

        // Handle shape selection
        almondShape.setOnClickListener { selectShape("Almond", R.drawable.almond_filled) }
        roundShape.setOnClickListener { selectShape("Round", R.drawable.round_filled) }
        squareShape.setOnClickListener { selectShape("Square", R.drawable.square_filled) }

        // Continue button click listener
        continueBooking.setOnClickListener {
            selectedShape?.let { navigateToNextFragment(it) }
        }
    }

    private fun selectShape(shape: String, filledDrawable: Int) {
        // Reset all images to unfilled state
        almondShape.setImageResource(R.drawable.almond_unfilled)
        roundShape.setImageResource(R.drawable.round_unfilled)
        squareShape.setImageResource(R.drawable.square_unfilled)

        // Highlight the selected shape
        when (shape) {
            "Almond" -> almondShape.setImageResource(filledDrawable)
            "Round" -> roundShape.setImageResource(filledDrawable)
            "Square" -> squareShape.setImageResource(filledDrawable)
        }

        selectedShape = shape
        continueBooking.isEnabled = true
        continueBooking.setBackgroundResource(R.drawable.continue_filled)
    }

    private fun navigateToNextFragment(shapeType: String) {
        val fragment = when (shapeType) {
            "Almond" -> BookingSelectLengthAlmondFragment()
            "Round" -> BookingSelectLengthRoundFragment()
            "Square" -> BookingSelectLengthSquareFragment()
            else -> return
        }

        fragment.arguments = Bundle().apply {
            putString("SERVICE_TYPE", serviceType)
            putString("SELECTED_DATE", selectedDate)
            putString("SELECTED_TIME", selectedTime)
            putString("SERVICE_PRICE", servicePrice)
            putString("SELECTED_SHAPE", shapeType) // Pass selected shape
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String, selectedTime: String, servicePrice: String) =
            BookingSelectShapeFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                    putString("SELECTED_TIME", selectedTime)
                    putString("SERVICE_PRICE", servicePrice)
                }
            }
    }
}
