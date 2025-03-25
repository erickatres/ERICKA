package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.R

class ReviewFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_form, container, false)

        val serviceTypeTextView = view.findViewById<TextView>(R.id.serviceName)

        val serviceType = arguments?.getString("service_type") ?: "Unknown"

        serviceTypeTextView.text = serviceType

        return view
    }
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisibility(false) // Hide bottom nav
    }
    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true) // Show bottom nav again when leaving
    }


}
