package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookyournailsmobile.R

class BookingSelectLengthRoundFragment : Fragment() {
    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var selectedShape: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceType = it.getString("SERVICE_TYPE")
            selectedDate = it.getString("SELECTED_DATE")
            selectedTime = it.getString("SELECTED_TIME")
            servicePrice = it.getString("SERVICE_PRICE")
            selectedShape = it.getString("SELECTED_SHAPE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.booking_select_length_round, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceType: String, selectedDate: String, selectedTime: String, servicePrice: String, selectedShape: String) =
            BookingSelectLengthRoundFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_DATE", selectedDate)
                    putString("SELECTED_TIME", selectedTime)
                    putString("SERVICE_PRICE", servicePrice)
                    putString("SELECTED_SHAPE", selectedShape)
                }
            }
    }
}
