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

class GelPolishFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout
    private lateinit var btnBook: Button
    private lateinit var gelPolishReviews: RecyclerView
    private lateinit var ratingTestimonials: TextView
    private lateinit var seeAllReviews: TextView
    private lateinit var starViews: List<ImageView>
    private lateinit var avrgRating: TextView

    private val imageList = listOf(
        R.drawable.gelpolish1,
        R.drawable.gelpolish2,
        R.drawable.gelpolish3,
        R.drawable.gelpolish4,
        R.drawable.gelpolish5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gel_polish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("GelPolishFragment", "Fragment created successfully")

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPagerGelPolish)
        tvImageCount = view.findViewById(R.id.tvImageCountGelPolish)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.btn_book)
        gelPolishReviews = view.findViewById(R.id.gelpolish_review)
        ratingTestimonials = view.findViewById(R.id.gelpolish_rating_testimonials)
        seeAllReviews = view.findViewById(R.id.gel_polish_seeallreviews)
        avrgRating = view.findViewById(R.id.tvAverageRating)

        starViews = listOf(
            view.findViewById(R.id.star1_gelpolish),
            view.findViewById(R.id.star2_gelpolish),
            view.findViewById(R.id.star3_gelpolish),
            view.findViewById(R.id.star4_gelpolish),
            view.findViewById(R.id.star5_gelpolish)
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
        gelPolishReviews.layoutManager = LinearLayoutManager(requireContext())

        // Fetch reviews for Gel Polish service
        fetchReviews("Gel Polish")

        // Back Button Click
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Book Button Click
        btnBook.setOnClickListener {
            navigateToAppointmentFragment("Gel Polish")
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
            Log.e("GelPolishFragment", "RetrofitClient.create() returned null")
            return
        }

        apiService.getReviewsByService(serviceType).enqueue(object : Callback<ApiService.ReviewResponse> {
            override fun onResponse(call: Call<ApiService.ReviewResponse>, response: Response<ApiService.ReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { reviewResponse ->
                        val allReviews = reviewResponse.reviews
                        if (allReviews.isNotEmpty()) {
                            val limitedReviews = allReviews.take(3)
                            gelPolishReviews.adapter = ReviewAdapter(limitedReviews)

                            // Show "See All Reviews" if there are more than 3 reviews
                            seeAllReviews.visibility = if (allReviews.size > 3) View.VISIBLE else View.GONE

                            // Display average rating and update stars
                            avrgRating.text = reviewResponse.average_rating
                            updateStarRating(reviewResponse.average_rating.toFloat())

                            // Click to see all reviews
                            seeAllReviews.setOnClickListener {
                                gelPolishReviews.adapter = ReviewAdapter(allReviews)
                                seeAllReviews.visibility = View.GONE
                            }
                        } else {
                            Log.d("GelPolishFragment", "No reviews found for $serviceType")
                            seeAllReviews.visibility = View.GONE
                        }
                    }
                } else {
                    Log.e("GelPolishFragment", "Failed to fetch reviews: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewResponse>, t: Throwable) {
                Log.e("GelPolishFragment", "Error fetching reviews", t)
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
