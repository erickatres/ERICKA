package com.example.bookyournailsmobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.bookyournailsmobile.Models.Booking
import com.example.bookyournailsmobile.R

class BookingAdapter(
    context: Context,
    private val bookings: List<Booking>,
    private val onRateClick: (Booking) -> Unit // Callback for button clicks
) : ArrayAdapter<Booking>(context, R.layout.booking_listview, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.booking_listview, parent, false)

        val booking = getItem(position)

        val serviceType = view.findViewById<TextView>(R.id.service_type)
        val serviceDate = view.findViewById<TextView>(R.id.service_date)
        val rateButton = view.findViewById<TextView>(R.id.btn_Rate)

        serviceType.text = booking?.service_type
        serviceDate.text = booking?.date_formatted



        // Prevent list item from being clickable
        view.isClickable = false
        view.isFocusable = false

        // Make rateButton clickable
        rateButton.isClickable = true
        rateButton.isFocusable = true

        // Set click listener for the rate button
        rateButton.setOnClickListener {
            booking?.let { onRateClick(it) }
        }

        return view
    }

    override fun getItem(position: Int): Booking? {
        return bookings[position]
    }

    override fun getCount(): Int {
        return bookings.size
    }
}
