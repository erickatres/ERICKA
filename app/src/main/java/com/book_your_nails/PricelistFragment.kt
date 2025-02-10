package com.book_your_nails

import AddOnsFragment
import GelPolishFragment
import RegularFragment
import RemovalFragment
import SoftGelExtensionFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class PricelistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pricelist, container, false)

        // Find TextViews
        val regularText = view.findViewById<TextView>(R.id.regular_text)
        val gelPolishText = view.findViewById<TextView>(R.id.gel_polish_text)
        val softGelText = view.findViewById<TextView>(R.id.soft_gel_text)
        val removalText = view.findViewById<TextView>(R.id.removal_text)
        val addOnsText = view.findViewById<TextView>(R.id.add_ons_text)

        // Set click listeners
        regularText.setOnClickListener { navigateToFragment(RegularFragment()) }
        gelPolishText.setOnClickListener { navigateToFragment(GelPolishFragment()) }
        softGelText.setOnClickListener { navigateToFragment(SoftGelExtensionFragment()) }
        removalText.setOnClickListener { navigateToFragment(RemovalFragment()) }
        addOnsText.setOnClickListener { navigateToFragment(AddOnsFragment()) }

        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}
