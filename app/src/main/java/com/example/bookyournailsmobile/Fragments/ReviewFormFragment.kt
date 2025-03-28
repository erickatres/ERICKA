package com.example.bookyournailsmobile.Fragments

import GelPolishFragment
import RegularFragment
import RemovalFragment
import SoftGelExtensionFragment
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewFormFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var sessionManagement: SessionManagement
    private var selectedRating = 0
    private lateinit var submitButton: Button
    private lateinit var reviewInput: EditText
    private lateinit var stars: List<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_form, container, false)

        // UI elements
        val serviceTypeTextView = view.findViewById<TextView>(R.id.serviceName)
        reviewInput = view.findViewById(R.id.reviewInput)
        submitButton = view.findViewById(R.id.submitButton)

        // Star Rating
        stars = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )

        // Get service type from arguments
        val serviceType = arguments?.getString("service_type") ?: "Unknown"
        serviceTypeTextView.text = serviceType

        // Initialize API Service and Session Management
        sessionManagement = SessionManagement(requireContext())
        apiService = RetrofitClient.create(requireContext())

        // Disable submit button initially
        submitButton.isEnabled = false

        // Handle star rating selection
        for (i in stars.indices) {
            stars[i].setOnClickListener {
                selectedRating = i + 1
                updateStarUI(selectedRating)
                checkSubmitButtonState()
            }
        }

        // Enable submit button only when review input is not empty
        reviewInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkSubmitButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle submit button click
        submitButton.setOnClickListener {
            val userId = sessionManagement.getUserId()
            val authToken = sessionManagement.getSessionToken() // Get token for Authorization header
            val reviewText = reviewInput.text.toString().trim()

            if (userId != null && authToken != null) {
                submitReview(userId, serviceType, selectedRating, reviewText, authToken)
            } else {
                Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateStarUI(rating: Int) {
        for (i in stars.indices) {
            stars[i].setImageResource(if (i < rating) R.drawable.star_filled else R.drawable.star_empty)
        }
    }

    private fun checkSubmitButtonState() {
        submitButton.isEnabled = selectedRating > 0 && reviewInput.text.toString().trim().isNotEmpty()
    }

    private fun submitReview(userId: String, service: String, rating: Int, reviewText: String, authToken: String) {
        val reviewRequest = ApiService.ReviewRequest(userId, service, rating, reviewText)
        val headers = mapOf("Authorization" to authToken)

        apiService.submitReview(headers, reviewRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                    resetForm()

                    when (service) {
                        "Regular Plain" -> navigateToRegularFragment()
                        "Removal" -> navigateToRemovalFragment()
                        "Gel Polish" -> navigateToGelPolishFragment()
                        "Soft Gel X" -> navigateToSoftGelExtensionFragment()
                        else -> parentFragmentManager.popBackStack()
                    }
                } else {
                    Log.e("ReviewFormFragment", "Response Code: ${response.code()}")
                    Log.e("ReviewFormFragment", "Error Body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ReviewFormFragment", "Network error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToSoftGelExtensionFragment() {
        val SoftGelExtensionFragment = SoftGelExtensionFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SoftGelExtensionFragment)
            .addToBackStack(null)
            .commit()

        // Ensure transaction completes before scrolling
        parentFragmentManager.executePendingTransactions()

        SoftGelExtensionFragment.view?.post {
            val scrollView = SoftGelExtensionFragment.view?.findViewById<NestedScrollView>(R.id.softgelx_scrollview)

            scrollView?.postDelayed({
                val y = scrollView.bottom
                ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }, 300)
        }
    }

    private fun navigateToGelPolishFragment() {
        val gelPolishFragment = GelPolishFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, gelPolishFragment)
            .addToBackStack(null)
            .commit()

        // Ensure transaction completes before scrolling
        parentFragmentManager.executePendingTransactions()

        gelPolishFragment.view?.post {
            val scrollView = gelPolishFragment.view?.findViewById<NestedScrollView>(R.id.gelpolish_scrollview)

            scrollView?.postDelayed({
                val y = scrollView.bottom
                ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }, 300)
        }
    }

    private fun navigateToRemovalFragment() {
        val removalFragment = RemovalFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, removalFragment)
            .addToBackStack(null)
            .commit()

        // Ensure transaction completes before scrolling
        parentFragmentManager.executePendingTransactions()

        removalFragment.view?.post {
            val scrollView = removalFragment.view?.findViewById<NestedScrollView>(R.id.removal_nested_scrollview)

            scrollView?.postDelayed({
                val y = scrollView.bottom
                ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }, 300)
        }
    }



    private fun navigateToRegularFragment() {
        val regularFragment = RegularFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, regularFragment)
            .addToBackStack(null)
            .commit()

        // Ensure transaction completes before trying to scroll
        parentFragmentManager.executePendingTransactions()

        regularFragment.view?.post {
            val scrollView = regularFragment.view?.findViewById<NestedScrollView>(R.id.nested_scroll_view)

            scrollView?.postDelayed({
                val y = scrollView.bottom // Get the bottom position
                ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                    duration = 500 // Adjust duration for speed (milliseconds)
                    interpolator = AccelerateDecelerateInterpolator() // Smooth transition
                    start()
                }
            }, 300) // Small delay to ensure UI is fully drawn before scrolling
        }
    }



    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisibility(false) // Hide bottom nav
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true) // Show bottom nav again when leaving
    }


    private fun resetForm() {
        selectedRating = 0
        updateStarUI(selectedRating)
        reviewInput.text.clear()
        submitButton.isEnabled = false
    }
}
