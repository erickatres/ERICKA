import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
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

        viewPager = view.findViewById(R.id.viewPagerRemoval)
        tvImageCount = view.findViewById(R.id.tvImageCountRemoval)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.btn_book)
        removalReviews = view.findViewById(R.id.removal_review)
        ratingTestimonials = view.findViewById(R.id.removal_rating_testimonials)
        seeAllReviews = view.findViewById(R.id.removal_see_all_reviews)

        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter
        tvImageCount.text = "1/${imageList.size}"

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvImageCount.text = "${position + 1}/${imageList.size}"
            }
        })

        removalReviews.layoutManager = LinearLayoutManager(requireContext())

        fetchReviews("Removal")

        btnBack.setOnClickListener {
            Log.d("RemovalFragment", "Back button clicked")
            parentFragmentManager.popBackStack()
        }

        btnBook.setOnClickListener {
            Log.d("RemovalFragment", "Book button clicked")
            navigateToAppointmentFragment("Removal")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

                            if (allReviews.size > 3) {
                                seeAllReviews.visibility = View.VISIBLE
                            } else {
                                seeAllReviews.visibility = View.GONE
                            }

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
