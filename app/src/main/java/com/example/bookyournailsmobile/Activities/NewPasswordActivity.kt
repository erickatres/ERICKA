package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.ResetPasswordRequest
import com.example.bookyournailsmobile.NetUtils.ResetPasswordResponse
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var btnBack: FrameLayout
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnResetPassword: Button
    private lateinit var apiService: ApiService

    private var passwordResetToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password) // Ensure correct layout file

        // Initialize views
        etNewPassword = findViewById(R.id.etnewpass)
        etConfirmPassword = findViewById(R.id.etConfirm)
        btnResetPassword = findViewById(R.id.btnContinue)
        btnBack = findViewById(R.id.btnBack)

        // Initialize Retrofit API Service
        apiService = RetrofitClient.create(this)

        // Get password reset token from Intent
        passwordResetToken = intent.getStringExtra("PASSWORD_RESET_TOKEN")

        // Check if token is missing
        if (passwordResetToken.isNullOrEmpty()) {
            showToast("Error: Invalid reset token")
            finish()
            return
        }

        Log.d("ResetPassword", "Token Received: $passwordResetToken")

        // Reset password button click listener
        btnResetPassword.setOnClickListener {
            resetPassword()
        }

        // Back button listener
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun resetPassword() {
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validate input fields
        if (!validatePassword(newPassword, confirmPassword)) return

        val resetPasswordRequest = ResetPasswordRequest(newPassword, confirmPassword)

        val headers = mapOf("Authorization" to "Bearer $passwordResetToken")

        Log.d("ResetPassword", "Sending Request: $resetPasswordRequest")

        apiService.resetPassword(headers, resetPasswordRequest).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                if (response.isSuccessful) {
                    val resetPasswordResponse = response.body()
                    if (resetPasswordResponse?.status == "success") {
                        showToast("Password changed successfully!")
                        navigateToLogin()
                    } else {
                        showToast(resetPasswordResponse?.message ?: "Failed to reset password")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    showToast("Error: $errorMessage")
                    Log.e("ResetPassword", "Response Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
                Log.e("ResetPassword", "Network Failure: ${t.message}")
            }
        })
    }

    private fun validatePassword(password: String, confirmPassword: String): Boolean {
        if (password.length < 8) {
            showToast("Password must be at least 8 characters long")
            return false
        }
        if (!password.matches(Regex(".*[A-Z].*"))) {
            showToast("Password must contain at least one uppercase letter")
            return false
        }
        if (!password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))) {
            showToast("Password must contain at least one special character")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
