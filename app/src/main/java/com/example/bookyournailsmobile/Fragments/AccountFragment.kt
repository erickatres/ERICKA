package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.R
import com.vishnusivadas.advanced_httpurlconnection.PutData

class AccountFragment : Fragment() {

    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var editFirstnameTextView: TextView
    private lateinit var editLastnameTextView: TextView
    private lateinit var saveChangesButton: Button
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize views
        firstnameEditText = view.findViewById(R.id.firstnameEditText)
        lastnameEditText = view.findViewById(R.id.lastnameEditText)
        editFirstnameTextView = view.findViewById(R.id.editFirstname)
        editLastnameTextView = view.findViewById(R.id.editLastname)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)

        sessionManagement = SessionManagement(requireContext())

        // Retrieve user data from SharedPreferences
        val user = requireContext().getUserFromPreferences()
        user?.let {
            firstnameEditText.hint = it.firstname
            lastnameEditText.hint = it.lastname
        }

        // Set underlined text for the "Edit" options
        editFirstnameTextView.text = Html.fromHtml("<u>Edit</u>")
        editLastnameTextView.text = Html.fromHtml("<u>Edit</u>")

        // Handle clicks to allow editing
        editFirstnameTextView.setOnClickListener {
            firstnameEditText.isFocusable = true
            firstnameEditText.isFocusableInTouchMode = true
            firstnameEditText.requestFocus()
        }

        editLastnameTextView.setOnClickListener {
            lastnameEditText.isFocusable = true
            lastnameEditText.isFocusableInTouchMode = true
            lastnameEditText.requestFocus()
        }

        // Make EditTexts non-editable initially
        firstnameEditText.isFocusable = false
        lastnameEditText.isFocusable = false

        // Save the changes
        saveChangesButton.setOnClickListener {
            val updatedFirstname = firstnameEditText.text.toString().trim()
            val updatedLastname = lastnameEditText.text.toString().trim()

            // Check if fields are empty
            if (updatedFirstname.isEmpty() || updatedLastname.isEmpty()) {
                Toast.makeText(requireContext(), "First name and last name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve the user ID from SharedPreferences
            val userId = sessionManagement.getSession()
            if (userId != null) {
                // Update the database via API call
                updateUserInDatabase(userId, updatedFirstname, updatedLastname)
            } else {
                Toast.makeText(requireContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
            }
        }

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Set up back button functionality
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }

    private fun updateUserInDatabase(userId: String, firstname: String, lastname: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val field = arrayOf("user_id", "first_name", "last_name")
            val data = arrayOf(userId, firstname, lastname)

            // Replace with your API endpoint
            val putData = PutData(
                "http://192.168.68.106/BookYourNails/public/update_user.php",
                "POST",
                field,
                data
            )

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    if (result == "Update Success") {
                        // Update SharedPreferences with new data
                        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("firstname", firstname)
                        editor.putString("lastname", lastname)
                        editor.apply()

                        // Update the hintText of EditText fields
                        activity?.runOnUiThread {
                            firstnameEditText.hint = firstname
                            lastnameEditText.hint = lastname

                            // Navigate back to ProfileFragment
                            parentFragmentManager.popBackStack()
                        }

                        // Show success message
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), "Log out to see changes", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show error message
                        Toast.makeText(requireContext(), "Failed to update profile: $result", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}