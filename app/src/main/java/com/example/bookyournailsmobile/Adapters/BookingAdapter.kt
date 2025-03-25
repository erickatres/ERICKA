package com.example.bookyournailsmobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.bookyournailsmobile.Models.Booking
import com.example.bookyournailsmobile.R
class BookingAdapter(
    context: Context,
    private val bookings: List<Booking> // List of Booking objects
) : ArrayAdapter<Booking>(context, R.layout.booking_listview, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.booking_listview, parent, false)

        val booking = getItem(position)

        val serviceType = view.findViewById<TextView>(R.id.service_type)
        val serviceDate = view.findViewById<TextView>(R.id.service_date)
        val rateButton = view.findViewById<ImageView>(R.id.btn_Rate)


        serviceType.text = booking?.service_type
        serviceDate.text = booking?.date_formatted

        // Change button drawable if status is "Completed"
        if (booking?.status == "Completed") {
            rateButton.setImageResource(R.drawable.my_rating_button) // Update with your drawable
        } else {
            rateButton.setImageResource(R.drawable.rate_button) // Use a default icon
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
