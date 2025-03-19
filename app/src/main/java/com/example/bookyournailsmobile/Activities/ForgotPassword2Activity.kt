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
import com.example.bookyournailsmobile.NetUtils.VerifyOtpResponse
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-server-url.com/") // Replace with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Back button listener
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Resend code click listener
        tvResendCode.setOnClickListener {
            // Implement resend code logic here
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
                val email = intent.getStringExtra("email") ?: ""
                verifyOtp(email, otpCode)
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

    private fun verifyOtp(email: String, otp: String) {
        apiService.verifyOtp(email, otp).enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    // OTP is correct, navigate to NewPasswordActivity
                    val intent = Intent(this@ForgotPassword2Activity, NewPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    // OTP is incorrect, show error message
                    Toast.makeText(this@ForgotPassword2Activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                // Handle network error
                Toast.makeText(this@ForgotPassword2Activity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}