package com.example.bookyournailsmobile.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bookyournailsmobile.Fragments.MyRatingFragment
import com.example.bookyournailsmobile.Models.Booking
import com.example.bookyournailsmobile.R

class BookingAdapter(
    context: Context,
    private val bookings: List<Booking>,
    private val onRateClick: (Booking) -> Unit
) : ArrayAdapter<Booking>(context, R.layout.booking_listview, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.booking_listview, parent, false)

        val serviceType = view.findViewById<TextView>(R.id.service_type)
        val serviceDate = view.findViewById<TextView>(R.id.service_date)
        val rateButton = view.findViewById<TextView>(R.id.btn_Rate)

        val booking = getItem(position)

        booking?.let {
            serviceType.text = it.service_type
            serviceDate.text = it.date_formatted

            // Reset button state to default
            rateButton.isEnabled = true
            rateButton.setBackgroundResource(android.R.color.transparent)

            // Update button state based on booking's review and status
            if (it.is_reviewed == 1) {
                rateButton.background = ContextCompat.getDrawable(context, R.drawable.myrating_button)
                rateButton.isEnabled = true
            } else {
                when (it.status) {
                    "Cancelled" -> {
                        rateButton.background = ContextCompat.getDrawable(context, R.drawable.cancelled_button)
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
            }

            // Set click listener to navigate based on button state
            rateButton.setOnClickListener {
                if (rateButton.isEnabled) {
                    if (booking.is_reviewed == 1) {
                        // Navigate to MyRatingFragment
                        val myRatingFragment = MyRatingFragment().apply {
                            arguments = Bundle().apply {
                                putInt("booking_id", booking.booking_id)
                                putString("service_type", booking.service_type)
                            }
                        }
                        val activity = context as? androidx.fragment.app.FragmentActivity
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, myRatingFragment)
                            ?.addToBackStack(null)
                            ?.commit()
                    } else if (booking.status == "Completed") {
                        // Navigate to ReviewFormFragment
                        onRateClick(booking)
                    }
                }
            }
        }
        return view
    }


    // Ensure to get item from the bookings list
    override fun getItem(position: Int): Booking? {
        return bookings.getOrNull(position)
    }

    override fun getCount(): Int {
        return bookings.size
    }
}