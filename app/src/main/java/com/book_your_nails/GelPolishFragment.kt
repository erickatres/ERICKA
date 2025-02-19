import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.book_your_nails.R

class GelPolishFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gel_polish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("GelPolishFragment", "Fragment created successfully")

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE



        // Initialize views
        viewPager = view.findViewById(R.id.viewPagerGelPolish)
        tvImageCount = view.findViewById(R.id.tvImageCountGelPolish)
        btnBack = view.findViewById(R.id.btnBack)

        // Ensure btnBack is visible
        btnBack.visibility = View.VISIBLE



        // Image List
        val imageList = listOf(
            R.drawable.gelpolish1,
            R.drawable.gelpolish2,
            R.drawable.gelpolish3,
            R.drawable.gelpolish4,
            R.drawable.gelpolish5
        )

        // Set Adapter
        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        // Initial Page Indicator
        tvImageCount.text = "1/${imageList.size}"

        // Page Change Listener
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvImageCount.text = "${position + 1}/${imageList.size}"
            }
        })
        // Back Button Click
        btnBack.setOnClickListener {
            Log.d("RegularPlainFragment", "Back button clicked")
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }
}

