package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R

class AccountFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var editUsernameTextView: TextView
    private lateinit var editFirstnameTextView: TextView
    private lateinit var editLastnameTextView: TextView
    private lateinit var saveChangesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize views (do this only once)
        usernameEditText = view.findViewById(R.id.usernameEditText)
        firstnameEditText = view.findViewById(R.id.firstnameEditText)
        lastnameEditText = view.findViewById(R.id.lastnameEditText)
        editUsernameTextView = view.findViewById(R.id.editUsername)
        editFirstnameTextView = view.findViewById(R.id.editFirstname)
        editLastnameTextView = view.findViewById(R.id.editLastname)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)

        // Set underlined text for the "Edit" options
        editUsernameTextView.text = Html.fromHtml("<u>Edit</u>")
        editFirstnameTextView.text = Html.fromHtml("<u>Edit</u>")
        editLastnameTextView.text = Html.fromHtml("<u>Edit</u>")

        // Handle clicks to allow editing
        editUsernameTextView.setOnClickListener {
            usernameEditText.isFocusable = true
            usernameEditText.isFocusableInTouchMode = true
            usernameEditText.requestFocus() // Optional: Bring up the keyboard
        }

        editFirstnameTextView.setOnClickListener {
            firstnameEditText.isFocusable = true
            firstnameEditText.isFocusableInTouchMode = true
            firstnameEditText.requestFocus() // Optional: Bring up the keyboard
        }

        editLastnameTextView.setOnClickListener {
            lastnameEditText.isFocusable = true
            lastnameEditText.isFocusableInTouchMode = true
            lastnameEditText.requestFocus() // Optional: Bring up the keyboard
        }

        // Retrieve user data from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val firstname = sharedPreferences.getString("firstname", "")
        val lastname = sharedPreferences.getString("lastname", "")

        // Set the retrieved data in the EditText fields
        usernameEditText.setText(username)
        firstnameEditText.setText(firstname)
        lastnameEditText.setText(lastname)

        // Make EditTexts non-editable initially
        usernameEditText.isFocusable = false
        firstnameEditText.isFocusable = false
        lastnameEditText.isFocusable = false

        // Save the changes
        saveChangesButton.setOnClickListener {
            val updatedUsername = usernameEditText.text.toString()
            val updatedFirstname = firstnameEditText.text.toString()
            val updatedLastname = lastnameEditText.text.toString()

            // Save updated data to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("username", updatedUsername)
            editor.putString("firstname", updatedFirstname)
            editor.putString("lastname", updatedLastname)
            editor.apply()

            // Make EditTexts non-editable again
            usernameEditText.isFocusable = false
            firstnameEditText.isFocusable = false
            lastnameEditText.isFocusable = false
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
}

