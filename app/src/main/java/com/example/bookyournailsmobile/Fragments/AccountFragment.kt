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
import com.example.bookyournailsmobile.NetUtils.Urls

class AccountFragment : Fragment() {

    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var editFirstnameTextView: TextView
    private lateinit var editLastnameTextView: TextView
    private lateinit var editEmailTextView: TextView
    private lateinit var editMobileNumberTextView: TextView
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
        emailEditText = view.findViewById(R.id.emailEditText)
        mobileNumberEditText = view.findViewById(R.id.Mobilenumber_edittext)
        editFirstnameTextView = view.findViewById(R.id.editFirstname)
        editLastnameTextView = view.findViewById(R.id.editLastname)
        editEmailTextView = view.findViewById(R.id.editUsername)
        editMobileNumberTextView = view.findViewById(R.id.editUsername2)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)

        sessionManagement = SessionManagement(requireContext())

        // Retrieve user data from SharedPreferences
        val user = requireContext().getUserFromPreferences()
        user?.let {
            firstnameEditText.hint = it.firstname
            lastnameEditText.hint = it.lastname
            emailEditText.hint = it.email
            mobileNumberEditText.hint = it.phone
        }

        // Set underlined text for the "Edit" options
        editFirstnameTextView.text = Html.fromHtml("<u>Edit</u>")
        editLastnameTextView.text = Html.fromHtml("<u>Edit</u>")
        editEmailTextView.text = Html.fromHtml("<u>Edit</u>")
        editMobileNumberTextView.text = Html.fromHtml("<u>Edit</u>")

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

        editEmailTextView.setOnClickListener {
            emailEditText.isFocusable = true
            emailEditText.isFocusableInTouchMode = true
            emailEditText.requestFocus()
        }

        editMobileNumberTextView.setOnClickListener {
            mobileNumberEditText.isFocusable = true
            mobileNumberEditText.isFocusableInTouchMode = true
            mobileNumberEditText.requestFocus()
        }

        // Make EditTexts non-editable initially
        firstnameEditText.isFocusable = false
        lastnameEditText.isFocusable = false
        emailEditText.isFocusable = false
        mobileNumberEditText.isFocusable = false

        // Save the changes
        saveChangesButton.setOnClickListener {
            val updatedFirstname = firstnameEditText.text.toString().trim()
            val updatedLastname = lastnameEditText.text.toString().trim()
            val updatedEmail = emailEditText.text.toString().trim()
            val updatedMobileNumber = mobileNumberEditText.text.toString().trim()

            // Check if at least one field is updated
            if (updatedFirstname.isEmpty() && updatedLastname.isEmpty() && updatedEmail.isEmpty() && updatedMobileNumber.isEmpty()) {
                Toast.makeText(requireContext(), "No changes detected.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve the user ID from SharedPreferences
            val userId = sessionManagement.getSession()
            if (userId != null) {
                // Update the database via API call
                updateUserInDatabase(userId, updatedFirstname, updatedLastname, updatedEmail, updatedMobileNumber)
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

    private fun updateUserInDatabase(userId: String, firstname: String, lastname: String, email: String, mobileNumber: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            // Prepare fields and data arrays
            val fields = mutableListOf<String>()
            val data = mutableListOf<String>()

            // Add user_id to fields and data
            fields.add("user_id")
            data.add(userId)

            // Add non-empty fields to the arrays
            if (firstname.isNotEmpty()) {
                fields.add("first_name")
                data.add(firstname)
            }
            if (lastname.isNotEmpty()) {
                fields.add("last_name")
                data.add(lastname)
            }
            if (email.isNotEmpty()) {
                fields.add("email")
                data.add(email)
            }
            if (mobileNumber.isNotEmpty()) {
                fields.add("mobile_number")
                data.add(mobileNumber)
            }

            // Convert lists to arrays
            val fieldArray = fields.toTypedArray()
            val dataArray = data.toTypedArray()

            // Replace with your API endpoint
            val putData = PutData(
                Urls.URL_CHANGE_ACCOUNT,
                "POST",
                fieldArray,
                dataArray
            )

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    if (result == "Update Success") {
                        // Update SharedPreferences with new data
                        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        if (firstname.isNotEmpty()) editor.putString("firstname", firstname)
                        if (lastname.isNotEmpty()) editor.putString("lastname", lastname)
                        if (email.isNotEmpty()) editor.putString("email", email)
                        if (mobileNumber.isNotEmpty()) editor.putString("mobileNumber", mobileNumber)

                        editor.apply()

                        // Update the hintText of EditText fields
                        activity?.runOnUiThread {
                            if (firstname.isNotEmpty()) firstnameEditText.hint = firstname
                            if (lastname.isNotEmpty()) lastnameEditText.hint = lastname
                            if (email.isNotEmpty()) emailEditText.hint = email
                            if (mobileNumber.isNotEmpty()) mobileNumberEditText.hint = mobileNumber

                            // Navigate back to ProfileFragment
                            parentFragmentManager.popBackStack()
                        }

                        // Show success message
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show error message
                        Toast.makeText(requireContext(), "Failed to update profile: $result", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}