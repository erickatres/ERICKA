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

class RegularFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var regularReviews: RecyclerView
    private lateinit var ratingTestimonials: TextView
    private lateinit var btnBack: FrameLayout
    private lateinit var btnBook: Button
    private lateinit var tvImageCount: TextView

    private val imageList = listOf(
        R.drawable.regularplain1,
        R.drawable.regularplain2,
        R.drawable.regularplain3,
        R.drawable.regularplain4,
        R.drawable.regularplain5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_regular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPagerRegular)
        tvImageCount = view.findViewById(R.id.tvImageCountRegular)
        ratingTestimonials = view.findViewById(R.id.regular_rating_testimonials)
        regularReviews = view.findViewById(R.id.regular_review)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.btn_book)

        // Setup Image Slider Adapter
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

        // Set up RecyclerView
        regularReviews.layoutManager = LinearLayoutManager(requireContext())

        // Fetch reviews for Regular Plain service
        fetchReviews("Regular Plain")

        // Back Button Click
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Book Button Click
        btnBook.setOnClickListener {
            navigateToAppointmentFragment("Regular Plain")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun fetchReviews(serviceType: String) {
        val apiService = RetrofitClient.create(requireContext())

        if (apiService == null) {
            Log.e("RegularFragment", "RetrofitClient.create() returned null")
            return
        }

        apiService.getReviewsByService(serviceType).enqueue(object : Callback<ApiService.ReviewResponse> {
            override fun onResponse(call: Call<ApiService.ReviewResponse>, response: Response<ApiService.ReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { reviewResponse ->
                        if (reviewResponse.reviews.isNotEmpty()) {
                            regularReviews.adapter = ReviewAdapter(reviewResponse.reviews)
                        } else {
                            Log.d("RegularFragment", "No reviews found for $serviceType")
                        }
                    }
                } else {
                    Log.e("RegularFragment", "Failed to fetch reviews: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewResponse>, t: Throwable) {
                Log.e("RegularFragment", "Error fetching reviews", t)
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
