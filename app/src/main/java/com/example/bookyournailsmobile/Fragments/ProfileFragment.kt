package com.example.bookyournailsmobile.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Activities.LoginActivity
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement


class ProfileFragment : Fragment() {

    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sessionManagement = SessionManagement(requireContext())

        val logoutButton = view.findViewById<TextView>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            sessionManagement.clearSession()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}
