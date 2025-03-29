package com.example.bookyournailsmobile.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookyournailsmobile.Activities.LoginActivity
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement
import eightbitlab.com.blurview.BlurView

class ProfileFragment : Fragment() {

    private lateinit var sessionManagement: SessionManagement
    private lateinit var TVUser: TextView
    private lateinit var blurView: BlurView
    private lateinit var IVAvatar: ImageView
    private lateinit var IVEditAvatar: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManagement = SessionManagement(requireContext())
        TVUser = view.findViewById(R.id.TVUser)
        IVAvatar = view.findViewById(R.id.IVAvatar) // Profile image
        IVEditAvatar = view.findViewById(R.id.IVEditAvatar) // Edit icon

        blurView = view.findViewById(R.id.blurView)
        setupBlurView()

        val user = requireContext().getUserFromPreferences()
        user?.let {
            val firstName = it.first_name
            val truncatedName = if (firstName.length > 10) {
                "${firstName.take(10)}..."
            } else {
                firstName
            }
            TVUser.text = truncatedName
        }

        // Click listener to open the gallery
        IVEditAvatar.setOnClickListener {
            openGallery()
        }

        val logoutButton = view.findViewById<TextView>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val TVAccount = view.findViewById<TextView>(R.id.TVAccount_profile)
        TVAccount.setOnClickListener {
            replaceFragment(AccountFragment())
        }
        val TVChangePassword = view.findViewById<TextView>(R.id.TVChangePassword)
        TVChangePassword.setOnClickListener {
            replaceFragment(ChangePasswordFragment())
        }
        val TVAboutUs = view.findViewById<TextView>(R.id.TVAboutUs)
        TVAboutUs.setOnClickListener {
            replaceFragment(AboutUsFragment())
        }
        val TVOurPolicies = view.findViewById<TextView>(R.id.TVOurPolicies)
        TVOurPolicies.setOnClickListener {
            replaceFragment(OurPoliciesFragment())
        }
        val TVFAQS = view.findViewById<TextView>(R.id.TVFAQS)
        TVFAQS.setOnClickListener {
            replaceFragment(FaqsFragment())
        }

        val policiesButton = view.findViewById<ImageView>(R.id.btnOurPolicies)
        policiesButton.setOnClickListener {
            replaceFragment(OurPoliciesFragment())
        }

        val changepassButton = view.findViewById<ImageView>(R.id.btnChangePassword)
        changepassButton.setOnClickListener {
            replaceFragment(ChangePasswordFragment())
        }

        val accountButton = view.findViewById<ImageView>(R.id.btnAccount)
        accountButton.setOnClickListener {
            replaceFragment(AccountFragment())
        }

        val aboutUsButton = view.findViewById<ImageView>(R.id.btnAboutUs)
        aboutUsButton.setOnClickListener {
            replaceFragment(AboutUsFragment())
        }

        val faqsButton = view.findViewById<ImageView>(R.id.btnFAQS)
        faqsButton.setOnClickListener {
            replaceFragment(FaqsFragment())
        }
    }

    private fun setupBlurView() {
        val radius = 20f
        val rootView = view ?: return

        blurView.setupWith(rootView.findViewById<ViewGroup>(R.id.main_content_profile))
            .setFrameClearDrawable(requireActivity().window.decorView.background)
            .setBlurRadius(radius)

        blurView.visibility = View.GONE
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun showLogoutConfirmationDialog() {
        blurView.visibility = View.VISIBLE
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            blurView.visibility = View.GONE
            alertDialog.dismiss()
        }

        btnYes.setOnClickListener {
            sessionManagement.clearSession()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        alertDialog.show()
    }

    // Open the gallery to select an image
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    // Handle the result of image selection
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            IVAvatar.setImageURI(imageUri) // Set selected image to profile image
        }
    }
}
