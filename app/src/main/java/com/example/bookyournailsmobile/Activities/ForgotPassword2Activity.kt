package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ForgotPassword2Activity : AppCompatActivity() {

    // Declare all views
    private lateinit var btnBack: FrameLayout
    private lateinit var tvEmailVerification: TextView
    private lateinit var tvEnterEmail: TextView
    private lateinit var tvEmailDescription: TextView
    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var tvDidntReceiveCode: TextView
    private lateinit var tvResendCode: TextView
    private lateinit var btnContinue: Button

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password2)

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        tvEmailVerification = findViewById(R.id.tvEmailVerification)
        tvEnterEmail = findViewById(R.id.tvEnterEmail)
        tvEmailDescription = findViewById(R.id.tvEmailDescription)
        etOtp1 = findViewById(R.id.etOtp1)
        etOtp2 = findViewById(R.id.etOtp2)
        etOtp3 = findViewById(R.id.etOtp3)
        etOtp4 = findViewById(R.id.etOtp4)
        tvDidntReceiveCode = findViewById(R.id.tvDidntReceiveCode)
        tvResendCode = findViewById(R.id.tvResendCode)
        btnContinue = findViewById(R.id.btnContinue)

        // Initialize Retrofit
        apiService = RetrofitClient.create(this)

        // Back button listener
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Resend code click listener
        tvResendCode.setOnClickListener {
            // Implement resend code logic here
            val email = intent.getStringExtra("EMAIL") ?: ""
            if (email.isNotEmpty()) {
                resendOtp(email)
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Auto move focus to next OTP field
        setupOtpInputs()

        // Continue button listener
        btnContinue.setOnClickListener {
            val otpCode = etOtp1.text.toString().trim() +
                    etOtp2.text.toString().trim() +
                    etOtp3.text.toString().trim() +
                    etOtp4.text.toString().trim()

            if (otpCode.length < 4) {
                Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            } else {
                val passwordResetToken = intent.getStringExtra("PASSWORD_RESET_TOKEN") ?: ""
                verifyResetCode(passwordResetToken, otpCode)
            }
        }
    }

    private fun setupOtpInputs() {
        val otpFields = listOf(etOtp1, etOtp2, etOtp3, etOtp4)
        for (i in otpFields.indices) {
            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpFields.size - 1) {
                        otpFields[i + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun verifyResetCode(passwordResetToken: String, code: String) {
        val verifyResetCodeRequest = ApiService.VerifyResetCodeRequest(code)

        // Add the passwordResetToken to the headers
        val headers = HashMap<String, String>()
        headers["Authorization"] = passwordResetToken

        apiService.verifyResetCode(headers, verifyResetCodeRequest).enqueue(object : Callback<ApiService.VerifyResetCodeResponse> {
            override fun onResponse(call: Call<ApiService.VerifyResetCodeResponse>, response: Response<ApiService.VerifyResetCodeResponse>) {
                if (response.isSuccessful) {
                    val verifyResetCodeResponse = response.body()
                    if (verifyResetCodeResponse != null) {
                        // Handle success
                        Toast.makeText(this@ForgotPassword2Activity, verifyResetCodeResponse.message, Toast.LENGTH_SHORT).show()

                        // Navigate to NewPasswordActivity
                        navigateToNewPasswordActivity(passwordResetToken)
                    } else {
                        // Handle failure
                        Toast.makeText(this@ForgotPassword2Activity, "Failed to verify code: Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle HTTP error
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid request."
                        404 -> "Token not found."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to verify code: ${response.message()}"
                    }
                    Toast.makeText(this@ForgotPassword2Activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.VerifyResetCodeResponse>, t: Throwable) {
                // Handle network error
                Toast.makeText(this@ForgotPassword2Activity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToNewPasswordActivity(passwordResetToken: String) {
        val intent = Intent(this, NewPasswordActivity::class.java)
        intent.putExtra("PASSWORD_RESET_TOKEN", passwordResetToken) // Pass the token
        startActivity(intent)
        finish() // Close the current activity
    }

    private fun resendOtp(email: String) {
        val forgotPasswordRequest = ApiService.ForgotPasswordRequest(email)

        apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ApiService.ForgotPasswordResponse> {
            override fun onResponse(call: Call<ApiService.ForgotPasswordResponse>, response: Response<ApiService.ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    val forgotPasswordResponse = response.body()
                    if (forgotPasswordResponse != null) {
                        Toast.makeText(this@ForgotPassword2Activity, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ForgotPassword2Activity, "Failed to resend OTP: Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Email is required."
                        404 -> "Email not found."
                        500 -> "Failed to resend OTP. Please try again later."
                        else -> "Failed to resend OTP: ${response.message()}"
                    }
                    Toast.makeText(this@ForgotPassword2Activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@ForgotPassword2Activity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}