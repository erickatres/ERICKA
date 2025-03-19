package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.R
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.example.bookyournailsmobile.NetUtils.Urls

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

                // Send OTP to the email
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
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val body = """{"email": "$email"}""".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(Urls.URL_FORGOT_PASSWORD) // Use your backend URL
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ForgotPasswordActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                        try {
                            // Log the raw response for debugging
                            println("Server Response: $responseBody")

                            val jsonResponse = JSONObject(responseBody)
                            val status = jsonResponse.getString("status")
                            val message = jsonResponse.getString("message")

                            if (status == "success") {
                                Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
                                // Transition to ForgotPassword2Activity
                                val intent = Intent(this@ForgotPasswordActivity, ForgotPassword2Activity::class.java)
                                intent.putExtra("EMAIL", email)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            // Log the exception for debugging
                            e.printStackTrace()
                            Toast.makeText(this@ForgotPasswordActivity, "Failed to parse server response: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}