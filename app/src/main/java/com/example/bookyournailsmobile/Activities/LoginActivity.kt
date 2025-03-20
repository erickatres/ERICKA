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
import androidx.fragment.app.FragmentTransaction
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.NetUtils.LoginRequest
import com.example.bookyournailsmobile.NetUtils.LoginResponse
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import eightbitlab.com.blurview.BlurView

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

        sessionManagement = SessionManagement(this)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.tl_email_login)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.tl_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signupBtn = findViewById<TextView>(R.id.tv_signup)
        btnForgotPassword = findViewById(R.id.login_forgot_password) // Initialize btnForgotPassword

        signupBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

        loginButton.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString().trim()
            val password = passwordInputLayout.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showValidationPopup("Email and Password are required")
            } else {
                val loginRequest = LoginRequest(email, password)
                val call = RetrofitClient.instance.login(loginRequest)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null && loginResponse.user != null) {
                                val user = loginResponse.user
                                sessionManagement.saveSession(user.getId() ?: "")

                                saveUserToPreferences(user)

                                Toast.makeText(
                                    applicationContext,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                showValidationPopup("Invalid user data")
                            }
                        } else {
                            showValidationPopup("Wrong email or password")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showValidationPopup("Error logging in. Please try again.")
                    }
                })

            }
        }
    }

    private fun setupBlurView() {
        val radius = 50f // Adjust the blur radius as needed

        // Set up the BlurView
        blurView.setupWith(findViewById<ViewGroup>(R.id.main_content))
            .setFrameClearDrawable(window.decorView.background)
//            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
//            .setHasFixedTransformationMatrix(true)

        // Initially hide the BlurView
        blurView.visibility = android.view.View.GONE
    }

    private fun showValidationPopup(message: String) {
        // Show the blur effect
        blurView.visibility = android.view.View.VISIBLE

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.login_failed_popup, null)

        // Define the width and height for the popup window
        val width = resources.getDimensionPixelSize(R.dimen.popup_width) // Define this dimension in your dimens.xml
        val height = resources.getDimensionPixelSize(R.dimen.popup_height) // Define this dimension in your dimens.xml

        // Set up the popup window with specific width and height
        val popupWindow = PopupWindow(
            popupView,
            width, // Use the defined width
            height, // Use the defined height
            true
        )

        // Prevent the popup from being dismissed when touching outside
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true
        popupWindow.setOnDismissListener {
            // Hide the blur effect when the popup is dismissed
            blurView.visibility = android.view.View.GONE
        }

        // Set up the OK button to dismiss the popup
        val btnPopupOk = popupView.findViewById<Button>(R.id.btn_back_login)
        btnPopupOk.setOnClickListener {
            // Hide the blur effect
            blurView.visibility = android.view.View.GONE

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