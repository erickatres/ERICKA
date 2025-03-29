package com.example.bookyournailsmobile.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import eightbitlab.com.blurview.BlurView

// Save user data to SharedPreferences
fun Context.saveUserToPreferences(user: User) {
    val sharedPreferences = this.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val userJson = gson.toJson(user)
    editor.putString("user_data", userJson)
    editor.apply()
}

// Save user ID to SharedPreferences
fun Context.saveUserIdToPreferences(userId: String) {
    val sharedPreferences = this.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("user_id", userId)
    editor.apply()
}

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManagement: SessionManagement
    private lateinit var blurView: BlurView
    private lateinit var btnForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_login)

        // Initialize BlurView
        blurView = findViewById(R.id.blurView)
        setupBlurView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SessionManagement
        sessionManagement = SessionManagement(this)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.tl_email_login)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.tl_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signupBtn = findViewById<TextView>(R.id.tv_signup)
        btnForgotPassword = findViewById(R.id.login_forgot_password)

        // Navigate to RegisterActivity
        signupBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Navigate to ForgotPasswordActivity
        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString().trim()
            val password = passwordInputLayout.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showValidationPopup("Email and Password are required")
            } else {
                val loginRequest = ApiService.LoginRequest(email, password)
                val apiService = RetrofitClient.create(this)
                val call = apiService.login(loginRequest)

                call.enqueue(object : Callback<ApiService.LoginResponse> {
                    override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null && loginResponse.user != null) {
                                val user = loginResponse.user
                                val sessionToken = loginResponse.session_token

                                Log.d("LoginActivity", "User ID: ${user.getId()}")
                                Log.d("LoginActivity", "Session Token: $sessionToken")

                                // Save the session token and user ID using SessionManagement
                                sessionManagement.saveSession(user.getId() ?: "", sessionToken)
                                Log.d("LoginActivity", "User ID saved: ${user.getId()}")

                                // Save user data to SharedPreferences (if needed)
                                saveUserToPreferences(user)

                                Toast.makeText(
                                    applicationContext,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate to MainActivity
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                showValidationPopup("Invalid user data")
                            }
                        } else {
                            showValidationPopup("Wrong email or password")
                        }
                    }

                    override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
                        showValidationPopup("Error logging in. Please try again.")
                        Log.e("LoginActivity", "Login failed: ${t.message}")
                    }
                })
            }
        }
    }

    // Set up the BlurView
    private fun setupBlurView() {
        val radius = 50f // Adjust the blur radius as needed

        blurView.setupWith(findViewById<ViewGroup>(R.id.main_content))
            .setFrameClearDrawable(window.decorView.background)
            .setBlurRadius(radius)

        // Initially hide the BlurView
        blurView.visibility = android.view.View.GONE
    }

    // Show a validation popup with a message
    private fun showValidationPopup(message: String) {
        blurView.visibility = android.view.View.VISIBLE

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.login_failed_popup, null)

        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_height)

        val popupWindow = PopupWindow(
            popupView,
            width,
            height,
            true
        )

        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true
        popupWindow.setOnDismissListener {
            blurView.visibility = android.view.View.GONE
        }

        val btnPopupOk = popupView.findViewById<Button>(R.id.btn_back_login)
        btnPopupOk.setOnClickListener {
            blurView.visibility = android.view.View.GONE

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

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val fadeIn = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
        popupView.startAnimation(fadeIn)
    }

    // Check if the user is already logged in
    override fun onStart() {
        super.onStart()
        checkSession()
    }

    private fun checkSession() {
        val userID = sessionManagement.getUserId()
        if (userID != null) {
            moveToMainActivity()
        }
    }

    // Navigate to MainActivity
    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    // Make the activity full screen
    private fun makeFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

}