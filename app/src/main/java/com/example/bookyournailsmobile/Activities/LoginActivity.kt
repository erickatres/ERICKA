package com.example.bookyournailsmobile.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData

fun Context.saveUserToPreferences(user: User) {
    val sharedPreferences = this.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val userJson = gson.toJson(user)
    editor.putString("user_data", userJson)
    editor.apply()
}

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManagement: SessionManagement

    private fun setErrorBorder(view: TextInputLayout, hasError: Boolean) {
        if (hasError) {
            view.boxStrokeColor = resources.getColor(android.R.color.holo_red_dark)
        } else {
            view.boxStrokeColor = resources.getColor(android.R.color.holo_blue_dark) // Default color
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManagement = SessionManagement(this)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.tl_email_login)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.tl_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signupBtn = findViewById<TextView>(R.id.tv_signup)

        // Initialize password toggle functionality
        val etPassword = passwordInputLayout.editText
        passwordInputLayout.setEndIconOnClickListener {
            val isPasswordVisible = etPassword?.transformationMethod == null
            if (isPasswordVisible) {
                // Hide password
                etPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.eye_password)
            } else {
                // Show password
                etPassword?.transformationMethod = null
                passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.eye_password)
            }
            // Move cursor to the end of the text
            etPassword?.setSelection(etPassword.text?.length ?: 0)
        }

        signupBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString().trim()
            val password = passwordInputLayout.editText?.text.toString().trim()

            var hasError = false

            if (email.isEmpty()) {
                setErrorBorder(emailInputLayout, true)
                Toast.makeText(applicationContext, "Email is required", Toast.LENGTH_SHORT).show()
                hasError = true
            } else {
                setErrorBorder(emailInputLayout, false)
            }

            if (password.isEmpty()) {
                setErrorBorder(passwordInputLayout, true)
                Toast.makeText(applicationContext, "Password is required", Toast.LENGTH_SHORT).show()
                hasError = true
            } else {
                setErrorBorder(passwordInputLayout, false)
            }

            if (!hasError) {
                // Proceed with login logic
                val handler = android.os.Handler()
                handler.post {
                    val field = arrayOf("email", "password")
                    val data = arrayOf(email, password)
                    val putData = PutData(
                        "http://192.168.68.106/BookYourNails/public/login_test.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            Log.d("LoginActivity", "API Response: $result") // Log the raw response

                            if (result == "null") {
                                Toast.makeText(applicationContext, "WRONG EMAIL OR PASSWORD", Toast.LENGTH_SHORT).show()
                            } else {
                                try {
                                    val gson = Gson()
                                    val user = gson.fromJson(result, User::class.java)
                                    Log.d("LoginActivity", "Parsed User: $user") // Log the parsed user object

                                    val userID = user.id
                                    if (userID != null) {
                                        sessionManagement.saveSession(userID)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Invalid user ID",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@post
                                    }

                                    saveUserToPreferences(user)

                                    Toast.makeText(
                                        applicationContext,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } catch (e: Exception) {
                                    Log.e("LoginActivity", "Error parsing user data", e)
                                    Toast.makeText(
                                        applicationContext,
                                        "Error logging in. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkSession()
    }

    private fun checkSession() {
        val userID = sessionManagement.getSession()
        if (userID != null) {
            moveToMainActivity()
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun makeFullScreen() {
        // Set the window to full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}