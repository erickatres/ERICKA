package com.example.bookyournailsmobile.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookyournailsmobile.Activities.LoginActivity
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement
import eightbitlab.com.blurview.BlurView

class ProfileFragment : Fragment() {

    private lateinit var sessionManagement: SessionManagement
    private lateinit var TVUser: TextView
    private lateinit var blurView: BlurView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManagement = SessionManagement(requireContext())
        TVUser = view.findViewById(R.id.TVUser)

        // Initialize BlurView
        blurView = view.findViewById(R.id.blurView)
        setupBlurView()

        val user = requireContext().getUserFromPreferences()
        user?.let {
            TVUser.text = it.first_name
        }

        // Logout Button
        val logoutButton = view.findViewById<TextView>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Policies Button
        val policiesButton = view.findViewById<ImageView>(R.id.btnOurPolicies)
        policiesButton.setOnClickListener {
            replaceFragment(OurPoliciesFragment())
        }

        // Change Password Button
        val changepassButton = view.findViewById<ImageView>(R.id.btnChangePassword)
        changepassButton.setOnClickListener {
            replaceFragment(ChangePasswordFragment())
        }

        // Account Button
        val accountButton = view.findViewById<ImageView>(R.id.btnAccount)
        accountButton.setOnClickListener {
            replaceFragment(AccountFragment())
        }

        // About Us Button
        val aboutUsButton = view.findViewById<ImageView>(R.id.btnAboutUs)
        aboutUsButton.setOnClickListener {
            replaceFragment(AboutUsFragment())
        }

        // FAQs Button
        val faqsButton = view.findViewById<ImageView>(R.id.btnFAQS)
        faqsButton.setOnClickListener {
            replaceFragment(FaqsFragment())
        }
    }

    private fun setupBlurView() {
        val radius = 20f // Adjust the blur radius as needed

        // Ensure the view is not null
        val rootView = view ?: return

        // Set up the BlurView
        blurView.setupWith(rootView.findViewById<ViewGroup>(R.id.main_content_profile))
            .setFrameClearDrawable(requireActivity().window.decorView.background)
            .setBlurRadius(radius)

        // Hide the BlurView initially
        blurView.visibility = View.GONE
    }

    // Helper function to replace fragments
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add to back stack for navigation
        fragmentTransaction.commit()
    }

    // Show a confirmation dialog for logout
    private fun showLogoutConfirmationDialog() {
        // Show the blur effect
        blurView.visibility = View.VISIBLE

        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)

        // Build the dialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView) // Set the custom layout
            .setCancelable(true) // Allow the user to dismiss the dialog by tapping outside
            .create()

        // Set up button click listeners
        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            // Hide the blur effect
            blurView.visibility = View.GONE
            alertDialog.dismiss() // Dismiss the dialog
        }

        btnYes.setOnClickListener {
            // Perform logout
            sessionManagement.clearSession()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Show the dialog
        alertDialog.show()
    }
}