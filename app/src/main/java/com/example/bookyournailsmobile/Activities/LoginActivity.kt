package com.example.bookyournailsmobile.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
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
}class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManagement: SessionManagement

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

        signupBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString().trim()
            val password = passwordInputLayout.editText?.text.toString().trim()

            var hasError = false
            var errorMessage = ""

            if (email.isEmpty()) {
                errorMessage = "Email is required"
                hasError = true
            } else if (password.isEmpty()) {
                errorMessage = "Password is required"
                hasError = true
            }

            if (hasError) {
                showValidationPopup(errorMessage)
            } else {
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
                                showValidationPopup("Wrong email or password")
                            } else {
                                try {
                                    val gson = Gson()
                                    val user = gson.fromJson(result, User::class.java)
                                    Log.d("LoginActivity", "Parsed User: $user") // Log the parsed user object

                                    val userID = user.id
                                    if (userID != null) {
                                        sessionManagement.saveSession(userID)
                                    } else {
                                        showValidationPopup("Invalid user ID")
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
                                    showValidationPopup("Error logging in. Please try again.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showValidationPopup(message: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.login_failed_popup,
            null)


        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set up the popup message

        // Set up the OK button to dismiss the popup
        val btnPopupOk = popupView.findViewById<Button>(R.id.btn_back_login)
        btnPopupOk.setOnClickListener {
            // Apply fade-out animation before dismissing the popup
            val fadeOut = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out)
            popupView.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    popupWindow.dismiss()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // Apply fade-in animation when the popup is shown
        val fadeIn = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
        popupView.startAnimation(fadeIn)
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