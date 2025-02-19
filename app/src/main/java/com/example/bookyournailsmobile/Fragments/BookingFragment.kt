package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R

class BookingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        view.findViewById<FrameLayout>(R.id.circle_plus_icon).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AppointmentFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
