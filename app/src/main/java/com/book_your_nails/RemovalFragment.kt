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

class RemovalFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_removal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RemovalFragment", "Fragment created successfully")



        // Initialize views
        viewPager = view.findViewById(R.id.viewPagerRemoval)
        tvImageCount = view.findViewById(R.id.tvImageCountRemoval)
        btnBack = view.findViewById(R.id.btnBack)

        // Ensure btnBack is visible
        btnBack.visibility = View.VISIBLE



        // Image List
        val imageList = listOf(
            R.drawable.removal1,
            R.drawable.removal2,
            R.drawable.removal3,
            R.drawable.removal4,
            R.drawable.removal5
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

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE
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

