package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bookyournailsmobile.R
import com.google.android.material.textfield.TextInputLayout
import com.vishnusivadas.advanced_httpurlconnection.PutData

class RegisterActivity : AppCompatActivity() {
    private val shownErrors = mutableSetOf<String>() // Track shown error messages
    private val shownSuccesses = mutableSetOf<String>() // Track shown success messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_register)

        // Use TextInputLayout
        val tilFirstname = findViewById<TextInputLayout>(R.id.tl_firstname)
        val tilLastname = findViewById<TextInputLayout>(R.id.tl_lastname)
        val tilEmail = findViewById<TextInputLayout>(R.id.tl_email)
        val tilMobileNumber = findViewById<TextInputLayout>(R.id.tl_mobilenumber)
        val tilPassword = findViewById<TextInputLayout>(R.id.tl_password)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tl_confirm_password)

        // Remove the error icon from the password field
        tilPassword.errorIconDrawable = null

        val registerBtn = findViewById<Button>(R.id.button_signup)
        val loginBack = findViewById<TextView>(R.id.tv_loginback)

        loginBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Real-time validation listeners for EditText inside TextInputLayout
        tilFirstname.editText?.addTextChangedListener(createTextWatcher { validateFirstname(it, tilFirstname) })
        tilLastname.editText?.addTextChangedListener(createTextWatcher { validateLastname(it, tilLastname) })
        tilEmail.editText?.addTextChangedListener(createTextWatcher { validateEmail(it, tilEmail) })
        tilMobileNumber.editText?.addTextChangedListener(createTextWatcher { validateMobileNumber(it, tilMobileNumber) })
        tilPassword.editText?.addTextChangedListener(createTextWatcher { validatePassword(it, tilPassword) })
        tilConfirmPassword.editText?.addTextChangedListener(createTextWatcher {
            validateConfirmPassword(it, tilPassword.editText?.text.toString(), tilConfirmPassword)
        })

        // Set click listener to remove error when password field is clicked
        tilPassword.editText?.setOnClickListener {
            if (tilPassword.error != null) {
                tilPassword.error = null // Remove error message
            }
        }

        registerBtn.setOnClickListener {
            val firstname = tilFirstname.editText?.text.toString().trim()
            val lastname = tilLastname.editText?.text.toString().trim()
            val email = tilEmail.editText?.text.toString().trim()
            val phone = tilMobileNumber.editText?.text.toString().trim()
            val password = tilPassword.editText?.text.toString().trim()
            val confirmPassword = tilConfirmPassword.editText?.text.toString().trim()

            // Validate all inputs
            if (validateInputs(
                    firstname,
                    lastname,
                    email,
                    phone,
                    password,
                    confirmPassword,
                    tilFirstname,
                    tilLastname,
                    tilEmail,
                    tilMobileNumber,
                    tilPassword,
                    tilConfirmPassword
                )
            ) {
                // Proceed with registration logic
                Toast.makeText(applicationContext, "✅ All inputs are valid!", Toast.LENGTH_SHORT).show()

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val field = arrayOf("first_name", "last_name", "email", "phone", "password")
                    val data = arrayOf(firstname, lastname, email, phone, password)

                    val putData = PutData("http://192.168.68.106/BookYourNails/public/signup.php", "POST", field, data)

                    if (putData.startPut() && putData.onComplete()) {
                        val result = putData.result
                        runOnUiThread {
                            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                            if (result == "Sign Up Success") {
                                // Show success popup dialog
                                showSuccessPopup()
                            }
                        }
                    }
                }
            }
        }
    }

    // Function to show success popup dialog
    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Account Created Successfully!")
        builder.setMessage("Your account has been created successfully. You can now log in.")
        builder.setPositiveButton("Back to Login") { dialog, _ ->
            // Navigate back to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close the current activity
            dialog.dismiss()
        }
        builder.setCancelable(false) // Prevent dismissing the dialog by tapping outside
        val dialog = builder.create()
        dialog.show()
    }

    // TextWatcher for real-time validation
    private fun createTextWatcher(validator: (String) -> Boolean): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                validator(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    // First name validation
    private fun validateFirstname(firstname: String, tilFirstname: TextInputLayout): Boolean {
        return if (firstname.isEmpty()) {
            tilFirstname.error = "First name is required"
            false
        } else {
            tilFirstname.error = null
            true
        }
    }

    // Last name validation
    private fun validateLastname(lastname: String, tilLastname: TextInputLayout): Boolean {
        return if (lastname.isEmpty()) {
            tilLastname.error = "Last name is required"
            false
        } else {
            tilLastname.error = null
            true
        }
    }

    // Email validation
    private fun validateEmail(email: String, tilEmail: TextInputLayout): Boolean {
        return if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Invalid Format"
            false
        } else {
            tilEmail.error = null
            true
        }
    }

    // Mobile number validation
    private fun validateMobileNumber(mobileNumber: String, tilMobileNumber: TextInputLayout): Boolean {
        return when {
            mobileNumber.isEmpty() -> {
                tilMobileNumber.error = "Mobile number is required"
                false
            }
            mobileNumber.length != 11 -> {
                tilMobileNumber.error = "Mobile number must be 11 digits"
                false
            }
            !mobileNumber.startsWith("09") -> {
                tilMobileNumber.error = "Mobile number must start with 09"
                false
            }
            else -> {
                tilMobileNumber.error = null
                true
            }
        }
    }

    // Strong password validation without error icon manipulation
    private fun validatePassword(password: String, tilPassword: TextInputLayout): Boolean {
        return when {
            password.length < 6 -> {
                tilPassword.error = "Password must be at least 6 characters"
                false
            }
            !password.matches(".*[A-Z].*".toRegex()) -> {
                tilPassword.error = "Password must contain an uppercase letter"
                false
            }
            !password.matches(".*\\d.*".toRegex()) -> {
                tilPassword.error = "Password must contain a number"
                false
            }
            !password.matches(".*[!@#\$%^&*].*".toRegex()) -> {
                tilPassword.error = "Password must contain a special character (!@#\$%^&*)"
                false
            }
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    // Confirm Password validation
    private fun validateConfirmPassword(confirmPassword: String, password: String, tilConfirmPassword: TextInputLayout): Boolean {
        return if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Please confirm your password"
            false
        } else if (confirmPassword != password) {
            tilConfirmPassword.error = "Passwords do not match"
            false
        } else {
            tilConfirmPassword.error = null
            true
        }
    }

    // Validate all fields before submitting
    private fun validateInputs(
        firstname: String,
        lastname: String,
        email: String,
        mobileNumber: String,
        password: String,
        confirmPassword: String,
        tilFirstname: TextInputLayout,
        tilLastname: TextInputLayout,
        tilEmail: TextInputLayout,
        tilMobileNumber: TextInputLayout,
        tilPassword: TextInputLayout,
        tilConfirmPassword: TextInputLayout
    ): Boolean {
        // Check if any of the fields are empty
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || mobileNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(applicationContext, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        val validFirstname = validateFirstname(firstname, tilFirstname)
        val validLastname = validateLastname(lastname, tilLastname)
        val validEmail = validateEmail(email, tilEmail)
        val validMobileNumber = validateMobileNumber(mobileNumber, tilMobileNumber)
        val validPassword = validatePassword(password, tilPassword)
        val validConfirmPassword = validateConfirmPassword(confirmPassword, password, tilConfirmPassword)

        // Check if all validations passed
        if (!validFirstname || !validLastname || !validEmail || !validMobileNumber || !validPassword || !validConfirmPassword) {
            Toast.makeText(applicationContext, "Invalid input", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to show toast messages
    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            if (!shownErrors.contains(message)) {
                Toast.makeText(applicationContext, "❌ $message", Toast.LENGTH_SHORT).show()
                shownErrors.add(message)
            }
        } else {
            if (!shownSuccesses.contains(message)) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                shownSuccesses.add(message)
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
}
