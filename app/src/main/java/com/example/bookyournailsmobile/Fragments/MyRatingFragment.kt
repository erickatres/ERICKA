package com.example.bookyournailsmobile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bookyournailsmobile.R

class MyRatingFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var myRatingText: TextView
    private lateinit var reviewCard: androidx.cardview.widget.CardView
    private lateinit var nailImage: ImageView
    private lateinit var nameClient: TextView
    private lateinit var starRating: LinearLayout
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var testimonial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve arguments if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_rating, container, false)

        // Initialize views
        backButton = view.findViewById(R.id.back)
        myRatingText = view.findViewById(R.id.myRating)
        reviewCard = view.findViewById(R.id.reviewCard)
        nailImage = view.findViewById(R.id.nailImage)
        nameClient = view.findViewById(R.id.nameClient)
        starRating = view.findViewById(R.id.starRating)
        star1 = view.findViewById(R.id.star1)
        star2 = view.findViewById(R.id.star2)
        star3 = view.findViewById(R.id.star3)
        star4 = view.findViewById(R.id.star4)
        star5 = view.findViewById(R.id.star5)
        testimonial = view.findViewById(R.id.Testimonal)


        val bookingId = arguments?.getString(ARG_PARAM1) ?: ""
        val serviceType = arguments?.getString(ARG_PARAM2) ?: ""

        // You can add click listeners or other view manipulations here
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(bookingId: String, serviceType: String) =
            MyRatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, bookingId)
                    putString(ARG_PARAM2, serviceType)
                }
            }

        private const val ARG_PARAM1 = "booking_id"
        private const val ARG_PARAM2 = "service_type"
    }
}