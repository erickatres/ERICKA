package com.book_your_nails

import RegularFragment
import GelPolishFragment
import SoftGelExtensionFragment
import RemovalFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.book_your_nails.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the regular_frame and set click listener
        val regularFrame = view.findViewById<View>(R.id.regular_frame)
        regularFrame.setOnClickListener {
            navigateToFragment(RegularFragment())
        }

        // Find the gel_polish_frame and set click listener
        val gelPolishFrame = view.findViewById<View>(R.id.gel_polish_frame)
        gelPolishFrame.setOnClickListener {
            navigateToFragment(GelPolishFragment())
        }

        // Find the soft_gel_extension_frame and set click listener
        val softGelExtensionFrame = view.findViewById<View>(R.id.soft_gel_extension_frame)
        softGelExtensionFrame.setOnClickListener {
            navigateToFragment(SoftGelExtensionFragment())
        }

        // Find the removal_frame and set click listener
        val removalFrame = view.findViewById<View>(R.id.removal_frame)
        removalFrame.setOnClickListener {
            navigateToFragment(RemovalFragment())
        }

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
