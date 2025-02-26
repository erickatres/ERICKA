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

class ProfileFragment : Fragment() {

    private lateinit var sessionManagement: SessionManagement

    private lateinit var TVUser: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sessionManagement = SessionManagement(requireContext())
        TVUser = view.findViewById(R.id.TVUser)

        val user = requireContext().getUserFromPreferences()
        user?.let {
            TVUser.text = it.firstname
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

        return view
    }

    // Helper function to replace fragments
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add to back stack for navigation
        fragmentTransaction.commit()
    }

    private fun showLogoutConfirmationDialog() {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)

        // Build the dialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView) // Set the custom layout
            .setCancelable(true) // Allow the user to dismiss the dialog by tapping outside
            .create()

        // Apply fade-in animation when the dialog is shown
        val fadeIn = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        dialogView.startAnimation(fadeIn)

        // Set up button click listeners
        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            // Apply fade-out animation before dismissing the dialog
            val fadeOut = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            dialogView.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    alertDialog.dismiss()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }

        btnYes.setOnClickListener {
            // Apply fade-out animation before dismissing the dialog and performing logout
            val fadeOut = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            dialogView.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    alertDialog.dismiss()
                    // Perform logout
                    sessionManagement.clearSession()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }

        // Show the dialog
        alertDialog.show()
    }
}