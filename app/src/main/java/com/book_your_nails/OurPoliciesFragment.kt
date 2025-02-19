import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.book_your_nails.R

class OurPoliciesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_our_policies, container, false)

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Set up the back button click listener
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack() // Pops the current fragment from the back stack and returns to the previous one
        }

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.VISIBLE
    }
}

