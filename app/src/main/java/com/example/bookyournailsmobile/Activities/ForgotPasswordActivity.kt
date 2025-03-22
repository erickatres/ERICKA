package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.NetUtils.ForgotPasswordRequest
import com.example.bookyournailsmobile.NetUtils.ForgotPasswordResponse
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    // Declare variables for all views
    private lateinit var btnBack: FrameLayout
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvEnterEmail: TextView
    private lateinit var tvEmailDescription: TextView
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        makeFullScreen()

        // Initialize all views
        btnBack = findViewById(R.id.btnBack)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvEnterEmail = findViewById(R.id.tvEnterEmail)
        tvEmailDescription = findViewById(R.id.tvEmailDescription)
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail)
        etEmail = findViewById(R.id.etEmail)
        btnContinue = findViewById(R.id.btnContinue)

        // Back button listener
        btnBack.setOnClickListener {
            onBackPressed() // Go back to the previous activity
        }

        // Continue button listener
        btnContinue.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                textInputLayoutEmail.error = "Email is required"
            } else if (!isValidEmail(email)) {
                textInputLayoutEmail.error = "Please enter a valid email"
            } else {
                // Clear any previous errors
                textInputLayoutEmail.error = null

                // Send OTP to the email using Retrofit
                sendOtpToEmail(email)
            }
        }
    }

    private fun makeFullScreen() {
        // Set the window to full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun isValidEmail(email: String): Boolean {
        // Simple email validation regex
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun sendOtpToEmail(email: String) {
        // Create the request body
        val forgotPasswordRequest = ForgotPasswordRequest(email)

        // Get the Retrofit instance
        val apiService = RetrofitClient.create(this)

        // Make the API call
        apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    val forgotPasswordResponse = response.body()
                    if (forgotPasswordResponse != null) {
                        // Handle success
                        Toast.makeText(this@ForgotPasswordActivity, forgotPasswordResponse.message, Toast.LENGTH_SHORT).show()

                        // Navigate to the next activity (e.g., ForgotPassword2Activity)
                        val intent = Intent(this@ForgotPasswordActivity, ForgotPassword2Activity::class.java)
                        intent.putExtra("EMAIL", email)
                        intent.putExtra("PASSWORD_RESET_TOKEN", forgotPasswordResponse.password_reset_token) // Pass the token to the next activity
                        startActivity(intent)
                    } else {
                        // Handle failure
                        Toast.makeText(this@ForgotPasswordActivity, "Failed to send OTP: Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle HTTP error
                    val errorMessage = when (response.code()) {
                        400 -> "Email is required."
                        404 -> "Email not found."
                        500 -> "Failed to send email. Please try again later."
                        else -> "Failed to send OTP: ${response.message()}"
                    }
                    Toast.makeText(this@ForgotPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                // Handle network failure
                Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}