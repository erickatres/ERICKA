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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    private lateinit var apiService: ApiService
    private lateinit var loyaltyPointsBox: RelativeLayout

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
        loyaltyPointsBox = view.findViewById(R.id.relativeLayout)

        apiService = RetrofitClient.create(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeFullScreen()
        setupButtonListeners(view)

        // Retrieve user data and set the first name to the TextView
        val user = requireContext().getUserFromPreferences()
        user?.let {
            val firstName = it.first_name
            val truncatedName = if (firstName.length > 10) {
                "${firstName.take(10)}..."
            } else {
                firstName
            }
            fetchLoyaltyPoints(it.getId())  // ✅ Uses the getter method correctly
            firstNameTextView.text = "$truncatedName!" // Add an exclamation mark
        }
    }

    private fun fetchLoyaltyPoints(userId: String) {
        apiService.getLoyaltyPoints(userId).enqueue(object : Callback<ApiService.LoyaltyPointsResponse> {
            override fun onResponse(call: Call<ApiService.LoyaltyPointsResponse>, response: Response<ApiService.LoyaltyPointsResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    val points = rawResponse?.loyaltyPoints ?: 0
                    Home_points_text.text = "$points points"

                    // Save to SharedPreferences
                    val sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putInt("loyalty_points", points).apply()
                } else {
                    Home_points_text.text = "0 points"
                }
            }

            override fun onFailure(call: Call<ApiService.LoyaltyPointsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to fetch points", Toast.LENGTH_SHORT).show()
                Home_points_text.text = "0 points"
            }
        })
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
        loyaltyPointsBox.setOnClickListener{
            replaceFragment(LoyaltyProgramHelp())
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
