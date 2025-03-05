package com.example.bookyournailsmobile.Fragments

import GelPolishFragment
import RegularFragment
import RemovalFragment
import SoftGelExtensionFragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Domain.User
import com.google.gson.Gson

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
    private lateinit var serviceRegular: ImageView
    private lateinit var serviceSoftGelExtension: ImageView
    private lateinit var serviceRemoval: ImageView
    private lateinit var serviceGelPolish: ImageView
    private lateinit var promoImage1: ImageView
    private lateinit var promoImage2: ImageView
    private lateinit var text_on_image_1: TextView
    private lateinit var Home_points_text: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        firstNameTextView = view.findViewById(R.id.greeting_text)

        // Initialize ProgressBars


        // Initialize Service ImageViews
        Home_points_text = view.findViewById(R.id.Home_points_text)
        serviceRegular = view.findViewById(R.id.Service_regular)
        serviceSoftGelExtension = view.findViewById(R.id.Service_soft_gel_extension)
        serviceRemoval = view.findViewById(R.id.Service_removal)
        serviceGelPolish = view.findViewById(R.id.Service_gelpolish)





        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeFullScreen()
        setupButtonListeners(view)





        // Retrieve user data and set the first name to the TextView
        val user = requireContext().getUserFromPreferences()
        user?.let {
            firstNameTextView.text = it.firstname
        }

        // Set up button click listeners after the delay
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
        serviceRegular.setOnClickListener {
            replaceFragment(RegularFragment())
        }

        // Service Soft Gel Extension Button
        serviceSoftGelExtension.setOnClickListener {
            replaceFragment(SoftGelExtensionFragment())
        }

        // Service Removal Button
        serviceRemoval.setOnClickListener {
            replaceFragment(RemovalFragment())
        }

        // Service Gel Polish Button
        serviceGelPolish.setOnClickListener {
            replaceFragment(GelPolishFragment())
        }

        // Make Service ImageViews visible after the delay
        serviceRegular.visibility = View.VISIBLE
        serviceSoftGelExtension.visibility = View.VISIBLE
        serviceRemoval.visibility = View.VISIBLE
        serviceGelPolish.visibility = View.VISIBLE
    }

    private fun showPromoImages() {
        // Make Promo ImageViews visible after the delay
        promoImage1.visibility = View.VISIBLE
        promoImage2.visibility = View.VISIBLE
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