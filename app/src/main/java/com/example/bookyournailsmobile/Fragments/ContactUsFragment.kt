package com.example.bookyournailsmobile.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.R

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the correct layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        // Hide bottom navigation
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation_container)
        bottomNav?.visibility = View.GONE

        // Set up the back button click listener
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set up Facebook logo click listener
        val facebookLogo = view.findViewById<ImageView>(R.id.facebookLogo)
        facebookLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/1AFTVw5Dzp/"))
            startActivity(intent)
        }

        // Set up Instagram logo click listener
        val instagramLogo = view.findViewById<ImageView>(R.id.InstagramLogo)
        instagramLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/nailscape.e/"))
            startActivity(intent)
        }

        // Set up TikTok logo click listener
        val tiktokLogo = view.findViewById<ImageView>(R.id.TiktokLogo)
        tiktokLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@nailscape.by.steph"))
            startActivity(intent)
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
