package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import android.content.Context
import com.example.bookyournailsmobile.Domain.User
import com.google.gson.Gson

// Define getUserFromPreferences as an extension function for Context
fun Context.getUserFromPreferences(): User? {
    val sharedPreferences = this.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val userJson = sharedPreferences.getString("user_data", null)
    return if (userJson != null) {
        val gson = Gson()
        gson.fromJson(userJson, User::class.java)
    } else {
        null
    }
}

class HomeFragment : Fragment() {

    private lateinit var firstNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        firstNameTextView = view.findViewById(R.id.greeting_text) // Ensure the ID matches your XML
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeFullScreen()

        // Retrieve user data and set the first name to the TextView
        val user = requireContext().getUserFromPreferences() // Use the extension function
        user?.let {
            firstNameTextView.text = it.firstname
        }
    }

    private fun makeFullScreen() {
        // Access the activity's window
        activity?.window?.let { window ->
            // Set the window to full screen
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            // Hide the system UI (status bar and navigation bar)

        }
    }
}