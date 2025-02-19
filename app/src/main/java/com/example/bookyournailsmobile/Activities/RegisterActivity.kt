package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vishnusivadas.advanced_httpurlconnection.PutData
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.example.bookyournailsmobile.R

class RegisterActivity : AppCompatActivity() {
    private val shownErrors = mutableSetOf<String>() // Track shown error messages
    private val shownSuccesses = mutableSetOf<String>() // Track shown success messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Use EditText
        val etFirstname = findViewById<EditText>(R.id.et_firstname)
        val etLastname = findViewById<EditText>(R.id.et_lastname)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)

        val registerBtn = findViewById<Button>(R.id.button_signup)
        val loginBack = findViewById<TextView>(R.id.tv_loginback)

        loginBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Real-time validation listeners for EditText
        etFirstname.addTextChangedListener(createTextWatcher { validateFirstname(it, etFirstname) })
        etLastname.addTextChangedListener(createTextWatcher { validateLastname(it, etLastname) })
        etEmail.addTextChangedListener(createTextWatcher { validateEmail(it, etEmail) })
        etPassword.addTextChangedListener(createTextWatcher { validatePassword(it, etPassword) })
        etConfirmPassword.addTextChangedListener(createTextWatcher {
            validateConfirmPassword(it, etPassword.text.toString(), etConfirmPassword)
        })

        registerBtn.setOnClickListener {
            val firstname = etFirstname.text.toString().trim()
            val lastname = etLastname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate all inputs
            if (validateInputs(firstname, lastname, email, password, confirmPassword, etFirstname, etLastname, etEmail, etPassword, etConfirmPassword)) {
                // Proceed with registration logic
                Toast.makeText(applicationContext, "✅ All inputs are valid!", Toast.LENGTH_SHORT).show()

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val field = arrayOf("first_name", "last_name", "email", "password")
                    val data = arrayOf(firstname, lastname, email, password)

                    val putData = PutData("http://192.168.68.113//BookYourNails/public/signup.php", "POST", field, data)

                    if (putData.startPut() && putData.onComplete()) {
                        val result = putData.result
                        runOnUiThread {
                            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                            if (result == "Sign Up Success") {
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    // TextWatcher for real-time validation
    private fun createTextWatcher(validator: (String) -> Boolean): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()
                validator(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    // First name validation
    private fun validateFirstname(firstname: String, etFirstname: EditText): Boolean {
        return if (firstname.isEmpty()) {
            setErrorBorder(etFirstname, true)
            showMessage("First name is required", true)
            false
        } else {
            setErrorBorder(etFirstname, false)
            true
        }
    }

    // Last name validation
    private fun validateLastname(lastname: String, etLastname: EditText): Boolean {
        return if (lastname.isEmpty()) {
            setErrorBorder(etLastname, true)
            showMessage("Last name is required", true)
            false
        } else {
            setErrorBorder(etLastname, false)
            true
        }
    }

    // Email validation
    private fun validateEmail(email: String, etEmail: EditText): Boolean {
        return if (email.isEmpty()) {
            setErrorBorder(etEmail, true)
            showMessage("Email is required", true)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorBorder(etEmail, true)
            showMessage("Invalid email format", true)
            false
        } else {
            setErrorBorder(etEmail, false)
            true
        }
    }

    // Strong password validation
    private fun validatePassword(password: String, etPassword: EditText): Boolean {
        return when {
            password.length < 6 -> {
                setErrorBorder(etPassword, true)
                showMessage("Password must be at least 6 characters", true)
                false
            }
            !password.matches(".*[A-Z].*".toRegex()) -> {
                setErrorBorder(etPassword, true)
                showMessage("Password must contain an uppercase letter", true)
                false
            }
            !password.matches(".*\\d.*".toRegex()) -> {
                setErrorBorder(etPassword, true)
                showMessage("Password must contain a number", true)
                false
            }
            !password.matches(".*[!@#\$%^&*].*".toRegex()) -> {
                setErrorBorder(etPassword, true)
                showMessage("Password must contain a special character (!@#\$%^&*)", true)
                false
            }
            else -> {
                setErrorBorder(etPassword, false)
                true
            }
        }
    }

    // Confirm Password validation
    private fun validateConfirmPassword(confirmPassword: String, password: String, etConfirmPassword: EditText): Boolean {
        return if (confirmPassword.isEmpty()) {
            setErrorBorder(etConfirmPassword, true)
            showMessage("Please confirm your password", true)
            false
        } else if (confirmPassword != password) {
            setErrorBorder(etConfirmPassword, true)
            showMessage("Passwords do not match", true)
            false
        } else {
            setErrorBorder(etConfirmPassword, false)
            true
        }
    }

    // Validate all fields before submitting
    private fun validateInputs(
        firstname: String, lastname: String, email: String, password: String, confirmPassword: String,
        etFirstname: EditText, etLastname: EditText, etEmail: EditText,
        etPassword: EditText, etConfirmPassword: EditText
    ): Boolean {
        // Check if any of the fields are empty
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(applicationContext, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        val validFirstname = validateFirstname(firstname, etFirstname)
        val validLastname = validateLastname(lastname, etLastname)
        val validEmail = validateEmail(email, etEmail)
        val validPassword = validatePassword(password, etPassword)
        val validConfirmPassword = validateConfirmPassword(confirmPassword, password, etConfirmPassword)

        // Check if all validations passed
        if (!validFirstname || !validLastname || !validEmail || !validPassword || !validConfirmPassword) {
            Toast.makeText(applicationContext, "Invalid input", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to set exclamation icon for EditText
    private fun setErrorBorder(view: EditText, hasError: Boolean) {
        if (hasError) {
            // Set exclamation mark icon on the right
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0)
        } else {
            // Remove exclamation mark icon
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
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
}