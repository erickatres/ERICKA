package com.example.bookyournailsmobile.Fragments

import GelPolishFragment
import RegularFragment
import RemovalFragment
import SoftGelExtensionFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookyournailsmobile.R
import android.content.Context
import com.example.bookyournailsmobile.Domain.User
import com.google.gson.Gson

// Extension function to retrieve user data from SharedPreferences
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
        firstNameTextView = view.findViewById(R.id.greeting_text) // Ensure ID matches XML
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeFullScreen()

        // Retrieve user data and set the first name to the TextView
        val user = requireContext().getUserFromPreferences()
        user?.let {
            firstNameTextView.text = it.firstname
        }

        // Set up button click listeners
        setupButtonListeners(view)
    }

    private fun makeFullScreen() {
        activity?.window?.let { window ->
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupButtonListeners(view: View) {
        // Service Regular Button
        view.findViewById<ImageView>(R.id.service_Regular)?.setOnClickListener {
            replaceFragment(RegularFragment())
        }

        // Service Soft Gel Extension Button
        view.findViewById<ImageView>(R.id.service_softgelextension)?.setOnClickListener {
            replaceFragment(SoftGelExtensionFragment())
        }

        // Service Removal Button
        view.findViewById<ImageView>(R.id.Service_removal)?.setOnClickListener {
            replaceFragment(RemovalFragment())
        }

        // Service Gel Polish Button
        view.findViewById<ImageView>(R.id.service_gelpolish)?.setOnClickListener {
            replaceFragment(GelPolishFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentName = fragment::class.java.simpleName
        println("Replacing fragment with: $fragmentName") // Log the fragment name

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null) // Optional: Add to back stack for navigation
            commitAllowingStateLoss() // Safer than commit() in some cases
        }
    }
}