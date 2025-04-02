import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bookyournailsmobile.Adapters.ImageSliderAdapter
import com.example.bookyournailsmobile.Adapters.ReviewAdapter
import com.example.bookyournailsmobile.Fragments.AppointmentFragment
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemovalFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout
    private lateinit var btnBook: Button
    private lateinit var removalReviews: RecyclerView
    private lateinit var ratingTestimonials: TextView
    private lateinit var seeAllReviews: TextView
    private lateinit var starViews: List<ImageView>
    private lateinit var avrgRating: TextView

    private val imageList = listOf(
        R.drawable.removal1,
        R.drawable.removal2,
        R.drawable.removal3,
        R.drawable.removal4,
        R.drawable.removal5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_removal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RemovalFragment", "Fragment created successfully")

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPagerRemoval)
        tvImageCount = view.findViewById(R.id.tvImageCountRemoval)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.btn_book)
        removalReviews = view.findViewById(R.id.removal_review)
        ratingTestimonials = view.findViewById(R.id.removal_rating_testimonials)
        seeAllReviews = view.findViewById(R.id.removal_see_all_reviews)
        avrgRating = view.findViewById(R.id.tvAverageRating)

        starViews = listOf(
            view.findViewById(R.id.star1_removal),
            view.findViewById(R.id.star2_removal),
            view.findViewById(R.id.star3_removal),
            view.findViewById(R.id.star4_removal),
            view.findViewById(R.id.star5_removal)
        )

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Setup Image Slider
        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        // Update image count display
        tvImageCount.text = "1/${imageList.size}"

        // Handle Page Change (Manual Sliding Only)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvImageCount.text = "${position + 1}/${imageList.size}"
            }
        })

        // Set up RecyclerView for reviews
        removalReviews.layoutManager = LinearLayoutManager(requireContext())

        // Fetch reviews for Removal service
        fetchReviews("Removal")

        // Back Button Click
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Book Button Click
        btnBook.setOnClickListener {
            navigateToAppointmentFragment("Removal")
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing to disable back press
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }

    private fun fetchReviews(serviceType: String) {
        val apiService = RetrofitClient.create(requireContext())

        if (apiService == null) {
            Log.e("RemovalFragment", "RetrofitClient.create() returned null")
            return
        }

        apiService.getReviewsByService(serviceType).enqueue(object : Callback<ApiService.ReviewResponse> {
            override fun onResponse(call: Call<ApiService.ReviewResponse>, response: Response<ApiService.ReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { reviewResponse ->
                        val allReviews = reviewResponse.reviews
                        if (allReviews.isNotEmpty()) {
                            val limitedReviews = allReviews.take(3)
                            removalReviews.adapter = ReviewAdapter(limitedReviews)

                            // Show "See All Reviews" if there are more than 3 reviews
                            seeAllReviews.visibility = if (allReviews.size > 3) View.VISIBLE else View.GONE

                            // Display average rating and update stars
                            avrgRating.text = reviewResponse.average_rating
                            updateStarRating(reviewResponse.average_rating.toFloat())

                            // Click to see all reviews
                            seeAllReviews.setOnClickListener {
                                removalReviews.adapter = ReviewAdapter(allReviews)
                                seeAllReviews.visibility = View.GONE
                            }
                        } else {
                            Log.d("RemovalFragment", "No reviews found for $serviceType")
                            seeAllReviews.visibility = View.GONE
                        }
                    }
                } else {
                    Log.e("RemovalFragment", "Failed to fetch reviews: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewResponse>, t: Throwable) {
                Log.e("RemovalFragment", "Error fetching reviews", t)
            }
        })
    }

    private fun updateStarRating(rating: Float) {
        val fullStar = R.drawable.star_filled
        val emptyStar = R.drawable.star_empty

        for (i in starViews.indices) {
            when {
                rating >= i + 1 -> starViews[i].setImageResource(fullStar) // Full star
                else -> starViews[i].setImageResource(emptyStar) // Empty star
            }
        }
    }

    private fun navigateToAppointmentFragment(serviceType: String) {
        val appointmentFragment = AppointmentFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, appointmentFragment)
            .addToBackStack(null)
            .commit()
    }
}
