package com.example.bookyournailsmobile.Fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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
    private var serviceType: String = "Unknown"
    private lateinit var EditTextCounter: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_form, container, false)

        // UI elements
        val serviceTypeTextView = view.findViewById<TextView>(R.id.serviceName)
        reviewInput = view.findViewById(R.id.reviewInput)
        submitButton = view.findViewById(R.id.submitButton)
        EditTextCounter = view.findViewById(R.id.charCounter)

        // Star Rating
        stars = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )

        // Get bookingId and serviceType from arguments
        val bookingId = arguments?.getInt("booking_id") ?: -1
        Log.d("ReviewFormFragment", "Received Booking ID: $bookingId")
        serviceType = arguments?.getString("service_type") ?: "Unknown"
        serviceTypeTextView.text = serviceType

        // Initialize API Service and Session Management
        sessionManagement = SessionManagement(requireContext())
        apiService = RetrofitClient.create(requireContext())

        // Disable submit button initially
        submitButton.isEnabled = false
        submitButton.setBackgroundResource(R.drawable.button_reviewform_disabled)

        // Handle star rating selection
        for (i in stars.indices) {
            stars[i].setOnClickListener {
                selectedRating = i + 1
                updateStarUI(selectedRating)
                checkSubmitButtonState()
            }
        }

        // Enable submit button only when review input is not empty and rating is selected
        reviewInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkSubmitButtonState()
                EditTextCounter.text = "${s?.length ?: 0}/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle submit button click
        submitButton.setOnClickListener {
            val userId = sessionManagement.getUserId()
            val authToken = sessionManagement.getSessionToken()
            val reviewText = reviewInput.text.toString().trim()

            if (userId != null && authToken != null) {
                submitReview(userId, serviceType, selectedRating, reviewText, authToken, bookingId)
            } else {
                Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisibility(false)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }

    private fun updateStarUI(rating: Int) {
        for (i in stars.indices) {
            stars[i].setImageResource(if (i < rating) R.drawable.star_filled else R.drawable.star_empty)
        }
    }

    private fun checkSubmitButtonState() {
        val isReviewNotEmpty = reviewInput.text.toString().trim().isNotEmpty()
        val isRatingSelected = selectedRating > 0

        submitButton.isEnabled = isReviewNotEmpty && isRatingSelected

        if (submitButton.isEnabled) {
            submitButton.setBackgroundResource(R.drawable.button_reviewform)
        } else {
            submitButton.setBackgroundResource(R.drawable.button_reviewform_disabled)
        }
    }

    private fun submitReview(userId: String, service: String, rating: Int, reviewText: String, authToken: String, bookingId: Int) {
        val reviewRequest = ApiService.ReviewRequest(
            user_id = userId,
            service = service,
            rating = rating,
            review_text = reviewText,
            booking_id = bookingId
        )

        val headers = mapOf("Authorization" to authToken)

        apiService.submitReview(headers, reviewRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    try {
                        Toast.makeText(requireContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show()

                        val resultBundle = Bundle().apply {
                            putBoolean("success", true)
                            putString("service_type", service)
                        }
                        setFragmentResult("reviewSubmission", resultBundle)

                        showFeedbackPopup()
                        resetForm()

                    } catch (e: Exception) {
                        Log.e("ReviewFormFragment", "Error processing response: ${e.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ReviewFormFragment", "Error Response: $errorBody")
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ReviewFormFragment", "Network error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetForm() {
        selectedRating = 0
        updateStarUI(selectedRating)
        reviewInput.text.clear()
        submitButton.isEnabled = false
        submitButton.setBackgroundResource(R.drawable.button_reviewform_disabled)
    }

    private fun showFeedbackPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_feedback)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(900, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val btnOk = dialog.findViewById<TextView>(R.id.btnOkay)
        btnOk.setOnClickListener {
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }

        dialog.show()
    }
}
