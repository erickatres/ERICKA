package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var btnBack: FrameLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_forgot_password)

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack)
        etEmail = findViewById(R.id.etEmail)
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail)
        btnContinue = findViewById(R.id.btn_Continue)

        // Back button listener
        btnBack.setOnClickListener { onBackPressed() }

        // Continue button listener with OTP request control
        btnContinue.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                textInputLayoutEmail.error = "Email is required"
            } else if (!isValidEmail(email)) {
                textInputLayoutEmail.error = "Please enter a valid email"
            } else {
                textInputLayoutEmail.error = null // Clear any previous errors

                if (btnContinue.isEnabled) {
                    btnContinue.isEnabled = false
                    btnContinue.alpha = 0.5f // Indicate the button is disabled

                    sendOtpToEmail(email)
                } else {
                    Toast.makeText(this, "OTP is still sending, please wait.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Email validation function
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    // Function to send OTP
    private fun sendOtpToEmail(email: String) {
        val apiService = RetrofitClient.create(this)
        val forgotPasswordRequest = ApiService.ForgotPasswordRequest(email)

        apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ApiService.ForgotPasswordResponse> {
            override fun onResponse(call: Call<ApiService.ForgotPasswordResponse>, response: Response<ApiService.ForgotPasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val forgotPasswordResponse = response.body()
                    Toast.makeText(this@ForgotPasswordActivity, forgotPasswordResponse?.message ?: "OTP sent successfully", Toast.LENGTH_SHORT).show()

                    // Navigate to the next activity
                    val intent = Intent(this@ForgotPasswordActivity, ForgotPassword2Activity::class.java)
                    intent.putExtra("EMAIL", email)
                    intent.putExtra("PASSWORD_RESET_TOKEN", forgotPasswordResponse?.password_reset_token)
                    startActivity(intent)
                } else {
                    handleFailure(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<ApiService.ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                resetButtonState()
            }
        })
    }

    // Handle API response errors
    private fun handleFailure(code: Int, message: String) {
        val errorMessage = when (code) {
            400 -> "Email is required."
            404 -> "Email not found."
            500 -> "Failed to send email. Please try again later."
            else -> "Failed to send OTP: $message"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        resetButtonState()
    }
    private fun makeFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    // Reset button state after failure
    private fun resetButtonState() {
        btnContinue.isEnabled = true
        btnContinue.alpha = 1.0f
    }
}
