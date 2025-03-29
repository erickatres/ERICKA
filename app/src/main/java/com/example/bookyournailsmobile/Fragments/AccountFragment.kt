package com.example.bookyournailsmobile.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.Editable
import android.text.TextWatcher

class AccountFragment : Fragment() {

    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var editFirstnameTextView: TextView
    private lateinit var editLastnameTextView: TextView
    private lateinit var editEmailTextView: TextView
    private lateinit var editMobileNumberTextView: TextView
    private lateinit var saveChangesButton: TextView
    private lateinit var sessionManagement: SessionManagement
    private lateinit var apiService: ApiService

    private var originalFirstname: String? = null
    private var originalLastname: String? = null
    private var originalEmail: String? = null
    private var originalMobileNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        saveChangesButton = view.findViewById(R.id.btnOkay)

        saveChangesButton.visibility = View.GONE
        sessionManagement = SessionManagement(requireContext())
        apiService = RetrofitClient.create(requireContext())

        // Retrieve user data from SharedPreferences
        val user = requireContext().getUserFromPreferences()
        user?.let {
            originalFirstname = it.first_name
            originalLastname = it.last_name
            originalEmail = it.email
            originalMobileNumber = it.phone

            firstnameEditText.setText(it.first_name)
            lastnameEditText.setText(it.last_name)
            emailEditText.setText(it.email)
            mobileNumberEditText.setText(it.phone)
        }

        addTextWatcher(firstnameEditText)
        addTextWatcher(lastnameEditText)
        addTextWatcher(emailEditText)
        addTextWatcher(mobileNumberEditText)

        // Set underlined text for the "Edit" labels
        val underlinedText = "<u>Edit</u>"
        editFirstnameTextView.text = Html.fromHtml(underlinedText)
        editLastnameTextView.text = Html.fromHtml(underlinedText)
        editEmailTextView.text = Html.fromHtml(underlinedText)
        editMobileNumberTextView.text = Html.fromHtml(underlinedText)

        // Enable editing on click
        editFirstnameTextView.setOnClickListener { enableEditing(firstnameEditText) }
        editLastnameTextView.setOnClickListener { enableEditing(lastnameEditText) }
        editEmailTextView.setOnClickListener { enableEditing(emailEditText) }
        editMobileNumberTextView.setOnClickListener { enableEditing(mobileNumberEditText) }

        // Save changes
        saveChangesButton.setOnClickListener { updateUserDetails() }

        // Hide bottom navigation
        activity?.findViewById<View>(R.id.bottom_navigation_container)?.visibility = View.GONE

        // Back button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<View>(R.id.bottom_navigation_container)?.visibility = View.VISIBLE
    }

    private fun enableEditing(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    }
    private fun toggleSaveButton() {
        val isModified = firstnameEditText.text.toString() != originalFirstname ||
                lastnameEditText.text.toString() != originalLastname ||
                emailEditText.text.toString() != originalEmail ||
                mobileNumberEditText.text.toString() != originalMobileNumber

        saveChangesButton.visibility = if (isModified) View.VISIBLE else View.GONE
    }
    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleSaveButton()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateUserDetails() {
        val updatedFirstname = firstnameEditText.text.toString().trim()
        val updatedLastname = lastnameEditText.text.toString().trim()
        val updatedEmail = emailEditText.text.toString().trim()
        val updatedMobileNumber = mobileNumberEditText.text.toString().trim()
        val userId = sessionManagement.getUserId()

        if (updatedFirstname.isEmpty() && updatedLastname.isEmpty() && updatedEmail.isEmpty() && updatedMobileNumber.isEmpty()) {
            Toast.makeText(requireContext(), "No changes detected.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            val request = ApiService.UpdateUserRequest(
                user_id = userId,
                first_name = if (updatedFirstname.isNotEmpty()) updatedFirstname else null,
                last_name = if (updatedLastname.isNotEmpty()) updatedLastname else null,
                email = if (updatedEmail.isNotEmpty()) updatedEmail else null,
                phone = if (updatedMobileNumber.isNotEmpty()) updatedMobileNumber else null
            )

            apiService.updateUser(request).enqueue(object : Callback<ApiService.UpdateUserResponse> {
                override fun onResponse(call: Call<ApiService.UpdateUserResponse>, response: Response<ApiService.UpdateUserResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            updateUserPreferences(updatedFirstname, updatedLastname, updatedEmail, updatedMobileNumber)
                            parentFragmentManager.popBackStack()
                            restartMainActivity()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "Failed to update profile: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }


                override fun onFailure(call: Call<ApiService.UpdateUserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUserPreferences(firstname: String, lastname: String, email: String, phone: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (firstname.isNotEmpty()) editor.putString("first_name", firstname)
        if (lastname.isNotEmpty()) editor.putString("last_name", lastname)
        if (email.isNotEmpty()) editor.putString("email", email)
        if (phone.isNotEmpty()) editor.putString("phone", phone)
        editor.apply()
    }
    private fun restartMainActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }
}
