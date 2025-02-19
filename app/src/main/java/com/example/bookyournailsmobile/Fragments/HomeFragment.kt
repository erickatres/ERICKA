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
    private lateinit var progressBar: ProgressBar
    private lateinit var serviceRegular: ImageView
    private lateinit var serviceSoftGelExtension: ImageView
    private lateinit var serviceRemoval: ImageView
    private lateinit var serviceGelPolish: ImageView
    private lateinit var promoImage1: ImageView
    private lateinit var promoImage2: ImageView
    private lateinit var text_on_image_1: TextView
    private lateinit var text_on_text_below_loyalty_1: TextView
    private lateinit var servicesText: TextView
    private lateinit var progressBarRegular: ProgressBar
    private lateinit var progressBarGelPolish: ProgressBar
    private lateinit var progressBarSoftGelExtension: ProgressBar
    private lateinit var progressBarRemoval: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        firstNameTextView = view.findViewById(R.id.greeting_text)
        progressBar = view.findViewById(R.id.progressBar)

        // Initialize ProgressBars
        progressBarRegular = view.findViewById(R.id.progressBar_Regular)
        progressBarGelPolish = view.findViewById(R.id.progressBar_Gel_polish)
        progressBarSoftGelExtension = view.findViewById(R.id.progressBar_Soft_gel_extension)
        progressBarRemoval = view.findViewById(R.id.progressBar_Removal)

        // Initialize Service ImageViews
        text_on_text_below_loyalty_1 = view.findViewById(R.id.text_below_loyalty)
        text_on_image_1 = view.findViewById(R.id.text_on_image_1)
        serviceRegular = view.findViewById(R.id.Service_regular)
        serviceSoftGelExtension = view.findViewById(R.id.Service_soft_gel_extension)
        serviceRemoval = view.findViewById(R.id.Service_removal)
        serviceGelPolish = view.findViewById(R.id.Service_gelpolish)

        // Initialize Promo ImageViews
        promoImage1 = view.findViewById(R.id.image_1)
        promoImage2 = view.findViewById(R.id.image_2)

        // Initialize TextViews
        servicesText = view.findViewById(R.id.services_text)


        // Initially hide the Service and Promo ImageViews and TextViews
        text_on_image_1.visibility = View.INVISIBLE
        text_on_text_below_loyalty_1.visibility = View.INVISIBLE
        serviceRegular.visibility = View.INVISIBLE
        serviceSoftGelExtension.visibility = View.INVISIBLE
        serviceRemoval.visibility = View.INVISIBLE
        serviceGelPolish.visibility = View.INVISIBLE
        promoImage1.visibility = View.INVISIBLE
        promoImage2.visibility = View.INVISIBLE
        servicesText.visibility = View.INVISIBLE
        text_on_image_1.visibility = View.INVISIBLE
        text_on_text_below_loyalty_1.visibility = View.INVISIBLE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeFullScreen()

        // Show the ProgressBar when loading starts
        progressBar.visibility = View.VISIBLE

        // Simulate loading (replace this with your actual loading logic)
        simulateLoading()

        // Retrieve user data and set the first name to the TextView
        val user = requireContext().getUserFromPreferences()
        user?.let {
            firstNameTextView.text = it.firstname
        }

        // Set up button click listeners after the delay
        Handler(Looper.getMainLooper()).postDelayed({
            setupButtonListeners(view)
            showPromoImages()
            showServicesText()
            showPromoText()

            // Show progress bars for 3 seconds
            showProgressBars()

            // Hide progress bars after 3 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressBars()
            }, 300)
        }, 500) // 3 seconds delay before setting up listeners and showing promo images
    }

    private fun showProgressBars() {
        progressBarRegular.visibility = View.VISIBLE
        progressBarGelPolish.visibility = View.VISIBLE
        progressBarSoftGelExtension.visibility = View.VISIBLE
        progressBarRemoval.visibility = View.VISIBLE
    }

    private fun hideProgressBars() {
        progressBarRegular.visibility = View.INVISIBLE
        progressBarGelPolish.visibility = View.INVISIBLE
        progressBarSoftGelExtension.visibility = View.INVISIBLE
        progressBarRemoval.visibility = View.INVISIBLE
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

    private fun showServicesText() {
        // Make Services TextView visible after the delay
        servicesText.visibility = View.VISIBLE
    }

    private fun showPromoText() {
        // Make Promo TextView visible after the delay
        text_on_image_1.visibility = View.VISIBLE
        text_on_text_below_loyalty_1.visibility = View.VISIBLE
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

    private fun simulateLoading() {
        // Simulate a delay for loading (replace this with your actual loading logic)
        Handler(Looper.getMainLooper()).postDelayed({
            // Hide the ProgressBar when loading is complete
            progressBar.visibility = View.GONE
        }, 1000) // 3 seconds delay for demonstration
    }
}