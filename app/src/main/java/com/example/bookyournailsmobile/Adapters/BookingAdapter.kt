package com.example.bookyournailsmobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bookyournailsmobile.Models.Booking
import com.example.bookyournailsmobile.R

class BookingAdapter(
    context: Context,
    private val bookings: List<Booking>,
    private val onRateClick: (Booking) -> Unit // Callback for button clicks
) : ArrayAdapter<Booking>(context, R.layout.booking_listview, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.booking_listview, parent, false)

        val serviceType = view.findViewById<TextView>(R.id.service_type)
        val serviceDate = view.findViewById<TextView>(R.id.service_date)
        val rateButton = view.findViewById<TextView>(R.id.btn_Rate)

        val booking = getItem(position)

        // Ensure `booking` is not null before accessing its properties
        booking?.let {
            serviceType.text = it.service_type
            serviceDate.text = it.date_formatted

            // Change button background based on booking status
            when (it.status) {
                "Cancelled" -> {
                    rateButton.background = ContextCompat.getDrawable(context, R.drawable.cancelled_button)
                    rateButton.isEnabled = false
                }
                "Reviewed" -> {
                    rateButton.background = ContextCompat.getDrawable(context, R.drawable.myrating_button)
                    rateButton.isEnabled = false
                }
                "Rejected" -> {
                    rateButton.background = ContextCompat.getDrawable(context, R.drawable.rejected_button)
                    rateButton.isEnabled = false
                }
                "Completed" -> {
                    rateButton.background = ContextCompat.getDrawable(context, R.drawable.rate_button)
                    rateButton.isEnabled = true
                }
                else -> {
                    rateButton.setBackgroundResource(android.R.color.transparent)
                    rateButton.isEnabled = false
                }
            }

            // Make rateButton clickable only if it's enabled
            if (rateButton.isEnabled) {
                rateButton.setOnClickListener { onRateClick(booking) }
            } else {
                rateButton.setOnClickListener(null) // Remove click listener if not enabled
            }
        } ?: run {
            // Handle the case when `booking` is null
            serviceType.text = "Unknown Service"
            serviceDate.text = "Unknown Date"
            rateButton.setBackgroundResource(android.R.color.transparent)
            rateButton.isEnabled = false
        }

        return view
    }

    override fun getItem(position: Int): Booking? {
        return bookings.getOrNull(position)
    }

    override fun getCount(): Int {
        return bookings.size
    }
}
