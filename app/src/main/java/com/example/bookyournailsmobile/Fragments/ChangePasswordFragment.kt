package com.example.bookyournailsmobile.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.LoginActivity
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {

    private lateinit var newPasswordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var updatePasswordButton: Button
    private lateinit var sessionManagement: SessionManagement
    private lateinit var apiService: ApiService
    private lateinit var oldPasswordLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        etOldPassword = view.findViewById(R.id.etOldPassword)
        newPasswordEditText = view.findViewById(R.id.etnewPassword)
        confirmPasswordEditText = view.findViewById(R.id.etconfirmPassword)
        updatePasswordButton = view.findViewById(R.id.updatePasswordButton)
        oldPasswordLayout = view.findViewById(R.id.textInputLayout)
        newPasswordLayout = view.findViewById(R.id.textInputLayout2)
        confirmPasswordLayout = view.findViewById(R.id.textInputLayout3)

        // Initialize SessionManagement & Retrofit API service
        sessionManagement = SessionManagement(requireContext())
        apiService = RetrofitClient.create(requireContext())

        // Set up back button functionality
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Handle Update Password button click
        updatePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val oldPassword = etOldPassword.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Clear previous errors
        oldPasswordLayout.error = null
        newPasswordLayout.error = null
        confirmPasswordLayout.error = null

        // Validate old password
        if (oldPassword.isEmpty()) {
            oldPasswordLayout.error = "Old password cannot be empty!"
            return
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            newPasswordLayout.error = "New password cannot be empty!"
            return
        } else if (!isValidPassword(newPassword)) {
            newPasswordLayout.error = "Password must be at least 8 characters long, contain a number, and a special character!"
            return
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.error = "Confirm password cannot be empty!"
            return
        } else if (newPassword != confirmPassword) {
            confirmPasswordLayout.error = "Passwords do not match!"
            return
        }

        val request = ApiService.ChangePasswordRequest(oldPassword, newPassword, confirmPassword)

        // Call API using Retrofit
        apiService.changePassword(request).enqueue(object : Callback<ApiService.ChangePasswordResponse> {
            override fun onResponse(
                call: Call<ApiService.ChangePasswordResponse>,
                response: Response<ApiService.ChangePasswordResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.message != null) {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        sessionManagement.clearSession()
                        startActivity(Intent(activity, LoginActivity::class.java))
                        activity?.finish()
                    } else {
                        Toast.makeText(requireContext(), result?.error ?: "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.ChangePasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#\$%^&*(),.?\":{}|<>]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }
}
