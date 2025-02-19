package com.book_your_nails

import AboutUsFragment
import AccountFragment
import ChangePasswordFragment
import FaqsFragment
import OurPoliciesFragment
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Find the TextView for Log Out
        val tvLogout: TextView = view.findViewById(R.id.TVLogout)

        // Set underlined text
        tvLogout.text = Html.fromHtml("<u>Log out</u>")

        // Set click listener to show confirmation dialog
        tvLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Find the ImageView buttons
        val btnAccount: ImageView = view.findViewById(R.id.btnAccount)
        val btnChangePassword: ImageView = view.findViewById(R.id.btnChangePassword)
        val btnAboutUs: ImageView = view.findViewById(R.id.btnAboutUs)
        val btnOurPolicies: ImageView = view.findViewById(R.id.btnOurPolicies)
        val btnFaqs: ImageView = view.findViewById(R.id.btnFAQS)

        // Set click listeners for navigation
        btnAccount.setOnClickListener { navigateToFragment(AccountFragment()) }
        btnChangePassword.setOnClickListener { navigateToFragment(ChangePasswordFragment()) }
        btnAboutUs.setOnClickListener { navigateToFragment(AboutUsFragment()) }
        btnOurPolicies.setOnClickListener { navigateToFragment(OurPoliciesFragment()) }
        btnFaqs.setOnClickListener { navigateToFragment(FaqsFragment()) }

        return view
    }

    // Function to show a custom logout confirmation dialog
    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Transparent background

        // Find buttons from the custom dialog
        val btnYes = dialogView.findViewById<TextView>(R.id.btnYes)
        val btnNo = dialogView.findViewById<TextView>(R.id.btnNo)

        // Handle Yes button click
        btnYes.setOnClickListener {
            Toast.makeText(requireContext(), "You have logged out!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss() // Close dialog
        }

        // Handle No button click
        btnNo.setOnClickListener {
            alertDialog.dismiss() // Close dialog
        }

        alertDialog.show() // Show the custom dialog
    }

    // Function to navigate to any fragment
    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // Replace with your container ID
        transaction.addToBackStack(null) // Allow back navigation
        transaction.commit()
    }
}
