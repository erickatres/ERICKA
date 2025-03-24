package com.example.bookyournailsmobile.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.bookyournailsmobile.R

class BookingAttachImageFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var uploadImageView: ImageView
    private lateinit var continueBookingButton: Button

    private var serviceType: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        servicePrice = getServicePrice(serviceType)
        Log.d(
            "BookingAttachImageFragment",
            "Service Type: $serviceType, Selected Time: $selectedTime, Service Price: $servicePrice"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageSize = (screenWidth * 0.5).toInt()
        val layoutParams = uploadImageView.layoutParams
        layoutParams.width = imageSize
        layoutParams.height = imageSize
        uploadImageView.layoutParams = layoutParams

        continueBookingButton.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.booking_attach_image, container, false)

        titleTextView = view.findViewById(R.id.title)
        subtitleTextView = view.findViewById(R.id.subtitle)
        uploadImageView = view.findViewById(R.id.upload_image)
        continueBookingButton = view.findViewById(R.id.continueBooking)

        uploadImageView.setOnClickListener {
            openGallery()
        }

        continueBookingButton.setOnClickListener {
            handleContinueButtonClick()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            if (selectedImageUri != null) {
                uploadImageView.setImageURI(selectedImageUri)
                Toast.makeText(requireContext(), "Image selected successfully!", Toast.LENGTH_SHORT).show()
                continueBookingButton.isEnabled = true
            } else {
                Toast.makeText(requireContext(), "Failed to load image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleContinueButtonClick() {
        if (uploadImageView.drawable != null) {
            Toast.makeText(requireContext(), "Proceeding to the next step...", Toast.LENGTH_SHORT).show()
            Log.d("BookingAttachImageFragment", "Proceeding with service: $serviceType, time: $selectedTime")
            navigateToSummaryRegularPlainFragment()
        } else {
            Toast.makeText(requireContext(), "Please upload an image first!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSummaryRegularPlainFragment() {
        val time = selectedTime ?: "Unknown"
        val date = arguments?.getString("SELECTED_DATE") ?: "Unknown"
        val price = servicePrice ?: "₱350"

        val summaryFragment = SummaryRegularPlainFragment.newInstance(
            serviceType ?: "Unknown",
            date,
            time,
            price,
            selectedImageUri.toString()
        )

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, summaryFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getServicePrice(serviceType: String?): String {
        return when (serviceType) {
            "Regular Plain" -> "₱450"
            "Gel Polish" -> "₱650"
            "Removal" -> "₱250"
            "Soft Gel X" -> "₱950"
            else -> "null"
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100

        @JvmStatic
        fun newInstance(serviceType: String, selectedTime: String) =
            BookingAttachImageFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_TYPE", serviceType)
                    putString("SELECTED_TIME", selectedTime)
                }
            }
    }
}