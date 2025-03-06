package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R


class ChangePasswordFragment : Fragment() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var updatePasswordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        newPasswordEditText = view.findViewById(R.id.etnewPassword)
        confirmPasswordEditText = view.findViewById(R.id.etconfirmPassword)
        updatePasswordButton = view.findViewById(R.id.updatePasswordButton)

        // Set up back button functionality
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Handle Update Password button click
        updatePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validate passwords
            if (newPassword != confirmPassword) {
                errorTextView.text = "Passwords do not match!"
                errorTextView.visibility = View.VISIBLE
            } else if (!isValidPassword(newPassword)) {
                errorTextView.text = "Password must be at least 8 characters long, contain a number, and a special character!"
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
                // Proceed with password update (you can replace this with actual logic to save the new password)
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()


            }
        }
    }

    // Check if the password is valid (at least 8 characters, contains a number, and a special character)
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#\$%^&*(),.?\":{}|<>]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }
}
