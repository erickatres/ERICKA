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

class GelPolishFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout
    private lateinit var btnBook: Button
    private lateinit var gelPolishReviews: RecyclerView
    private lateinit var ratingTestimonials: TextView
    private lateinit var seeallReviews: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gel_polish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("GelPolishFragment", "Fragment created successfully")

        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Initialize views
        viewPager = view.findViewById(R.id.viewPagerGelPolish)
        tvImageCount = view.findViewById(R.id.tvImageCountGelPolish)
        btnBack = view.findViewById(R.id.btnBack)
        btnBook = view.findViewById(R.id.btn_book)
        gelPolishReviews = view.findViewById(R.id.gelpolish_review)
        ratingTestimonials = view.findViewById(R.id.gelpolish_rating_testimonials)
        seeallReviews = view.findViewById(R.id.gel_polish_seeallreviews)

        btnBack.visibility = View.VISIBLE

        val imageList = listOf(
            R.drawable.gelpolish1,
            R.drawable.gelpolish2,
            R.drawable.gelpolish3,
            R.drawable.gelpolish4,
            R.drawable.gelpolish5
        )

        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        tvImageCount.text = "1/${imageList.size}"

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvImageCount.text = "${position + 1}/${imageList.size}"
            }
        })

        gelPolishReviews.layoutManager = LinearLayoutManager(requireContext())

        // Fetch reviews for Gel Polish service
        fetchReviews("Gel Polish")

        btnBack.setOnClickListener {
            Log.d("GelPolishFragment", "Back button clicked")
            parentFragmentManager.popBackStack()
        }

        btnBook.setOnClickListener {
            Log.d("GelPolishFragment", "Book button clicked")
            navigateToAppointmentFragment("Gel Polish")
        }
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
                            val limitedReviews = allReviews.take(3) // Show only 3 reviews initially
                            gelPolishReviews.adapter = ReviewAdapter(limitedReviews)

                            // Show "See All Reviews" if there are more than 3 reviews
                            if (allReviews.size > 3) {
                                seeallReviews.visibility = View.VISIBLE
                            } else {
                                seeallReviews.visibility = View.GONE
                            }

                            // Expand reviews when "See All Reviews" is clicked
                            seeallReviews.setOnClickListener {
                                gelPolishReviews.adapter = ReviewAdapter(allReviews) // Show all reviews
                                seeallReviews.visibility = View.GONE // Hide button after expanding
                            }
                        } else {
                            Log.d("GelPolishFragment", "No reviews found for $serviceType")
                            seeallReviews.visibility = View.GONE
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

    private fun navigateToAppointmentFragment(serviceType: String) {
        val appointmentFragment = AppointmentFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType) // Pass the service type to AppointmentFragment
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, appointmentFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }
}
