package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookyournailsmobile.Adapters.ReviewAdapter
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeeAllReviewsFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var reviewsRecyclerView: RecyclerView
    private var serviceType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve serviceType from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_see_all_reviews, container, false)

        // Initialize UI components
        backButton = view.findViewById(R.id.back)
        reviewsRecyclerView = view.findViewById(R.id.regularPolishReviews)
        reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set click listener for the back button
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Fetch reviews using serviceType
        serviceType?.let {
            fetchAllReviews(it)
        }

        return view
    }

    private fun fetchAllReviews(serviceType: String) {
        val apiService = RetrofitClient.create(requireContext())

        if (apiService == null) {
            Log.e("SeeAllReviewsFragment", "RetrofitClient.create() returned null")
            return
        }

        apiService.getReviewsByService(serviceType).enqueue(object : Callback<ApiService.ReviewResponse> {
            override fun onResponse(call: Call<ApiService.ReviewResponse>, response: Response<ApiService.ReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { reviewResponse ->
                        val allReviews = reviewResponse.reviews
                        if (allReviews.isNotEmpty()) {
                            reviewsRecyclerView.adapter = ReviewAdapter(allReviews)
                        } else {
                            Log.d("SeeAllReviewsFragment", "No reviews found for $serviceType")
                        }
                    }
                } else {
                    Log.e("SeeAllReviewsFragment", "Failed to fetch reviews: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewResponse>, t: Throwable) {
                Log.e("SeeAllReviewsFragment", "Error fetching reviews", t)
            }
        })
    }
}
