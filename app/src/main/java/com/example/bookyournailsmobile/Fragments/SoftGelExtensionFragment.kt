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
class SoftGelExtensionFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout
    private lateinit var btnBook: Button
    private lateinit var reviewList: RecyclerView
    private lateinit var tvNoReviewsYet: TextView
    private lateinit var seeAllReviews: TextView
    private lateinit var starViews: List<ImageView>
    private lateinit var avrgRating: TextView

    private val imageList = listOf(
        R.drawable.softgel1,
        R.drawable.softgel2,
        R.drawable.softgel3,
        R.drawable.softgel4,
        R.drawable.softgel5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_soft_gel_extension, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("SoftGelExtensionFragment", "Fragment created successfully")

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPagerSoftGelExtension)
        tvImageCount = view.findViewById(R.id.tvImageCountSoftGelExtension)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.book_now)
        reviewList = view.findViewById(R.id.softgelx_review)
        tvNoReviewsYet = view.findViewById(R.id.tvNoReviewsYet)
        seeAllReviews = view.findViewById(R.id.softgelx_seeallreviews)
        avrgRating = view.findViewById(R.id.tvAverageRating)

        starViews = listOf(
            view.findViewById(R.id.star1_softgelx),
            view.findViewById(R.id.star2_softgelx),
            view.findViewById(R.id.star3_softgelx),
            view.findViewById(R.id.star4_softgelx),
            view.findViewById(R.id.star5_softgelx)
        )

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Setup Image Slider
        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        // Update image count display
        tvImageCount.text = "1/${imageList.size}"

        // Handle Page Change
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvImageCount.text = "${position + 1}/${imageList.size}"
            }
        })

        // Set up RecyclerView for reviews
        reviewList.layoutManager = LinearLayoutManager(requireContext())

        // Fetch reviews for Soft Gel X
        fetchReviews("Soft Gel X")

        // Back Button Click
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Book Button Click
        btnBook.setOnClickListener {
            navigateToAppointmentFragment("Soft Gel X")
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
            Log.e("SoftGelExtensionFragment", "RetrofitClient.create() returned null")
            return
        }

        apiService.getReviewsByService(serviceType).enqueue(object : Callback<ApiService.ReviewResponse> {
            override fun onResponse(call: Call<ApiService.ReviewResponse>, response: Response<ApiService.ReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { reviewResponse ->
                        val allReviews = reviewResponse.reviews
                        if (allReviews.isNotEmpty()) {
                            val limitedReviews = allReviews.take(3)
                            reviewList.adapter = ReviewAdapter(limitedReviews)

                            // Show "See All Reviews" if more than 3
                            seeAllReviews.visibility = if (allReviews.size > 3) View.VISIBLE else View.GONE

                            // Update star rating
                            avrgRating.text = reviewResponse.average_rating
                            updateStarRating(reviewResponse.average_rating.toFloat())

                            // Hide "No Reviews Yet" if reviews exist
                            tvNoReviewsYet.visibility = View.GONE

                            // Click to see all reviews
                            seeAllReviews.setOnClickListener {
                                reviewList.adapter = ReviewAdapter(allReviews)
                                seeAllReviews.visibility = View.GONE
                            }
                        } else {
                            // Show "No Reviews Yet" when there are no reviews
                            tvNoReviewsYet.visibility = View.VISIBLE
                            seeAllReviews.visibility = View.GONE
                        }
                    }
                } else {
                    Log.e("SoftGelExtensionFragment", "Failed to fetch reviews: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewResponse>, t: Throwable) {
                Log.e("SoftGelExtensionFragment", "Error fetching reviews", t)
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

