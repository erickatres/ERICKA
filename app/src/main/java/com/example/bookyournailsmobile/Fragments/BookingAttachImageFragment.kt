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
import com.example.bookyournailsmobile.Fragments.SummaryRegularPlainFragment


class BookingAttachImageFragment : Fragment() {

    // Declare views
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var uploadImageView: ImageView
    private lateinit var continueBookingButton: Button

    // Variables to store passed data
    private var serviceType: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null // Add service price variable
    private var selectedImageUri: Uri? = null // Add variable to store the selected image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the service type and selected time from arguments
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        // Determine the service price based on the service type
        servicePrice = getServicePrice(serviceType)
        Log.d(
            "BookingAttachImageFragment",
            "Service Type: $serviceType, Selected Time: $selectedTime, Service Price: $servicePrice"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the screen width
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Set the uploadImageView size to 50% of the screen width
        val imageSize = (screenWidth * 0.5).toInt()
        val layoutParams = uploadImageView.layoutParams
        layoutParams.width = imageSize
        layoutParams.height = imageSize
        uploadImageView.layoutParams = layoutParams

        // Initially disable the continue button
        continueBookingButton.isEnabled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.booking_attach_image, container, false)

        // Initialize views
        titleTextView = view.findViewById(R.id.title)
        subtitleTextView = view.findViewById(R.id.subtitle)
        uploadImageView = view.findViewById(R.id.upload_image)
        continueBookingButton = view.findViewById(R.id.continueBooking)

        // Set click listener for the upload image view
        uploadImageView.setOnClickListener {
            openGallery()
        }

        // Set click listener for the continue button
        continueBookingButton.setOnClickListener {
            // Handle continue button click
            handleContinueButtonClick()
        }

        return view
    }

    // Function to open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    // Handle the result of the gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            if (selectedImageUri != null) {
                // Set the selected image to the uploadImageView
                uploadImageView.setImageURI(selectedImageUri)
                Toast.makeText(requireContext(), "Image selected successfully!", Toast.LENGTH_SHORT).show()

                // Enable the continue button after image is selected
                continueBookingButton.isEnabled = true
            } else {
                Toast.makeText(requireContext(), "Failed to load image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle continue button click
    private fun handleContinueButtonClick() {
        // Check if an image has been selected
        if (uploadImageView.drawable != null) {
            // Proceed to the next step (e.g., navigate to another fragment or activity)
            Toast.makeText(requireContext(), "Proceeding to the next step...", Toast.LENGTH_SHORT).show()
            Log.d("BookingAttachImageFragment", "Proceeding with service: $serviceType, time: $selectedTime")

            // Navigate to SummaryRegularPlainFragment
            navigateToSummaryRegularPlainFragment()
        } else {
            Toast.makeText(requireContext(), "Please upload an image first!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to navigate to SummaryRegularPlainFragment
    private fun navigateToSummaryRegularPlainFragment() {
        // Ensure selectedTime, selectedDate, and servicePrice are not null
        val time = selectedTime ?: "Unknown"
        val date = arguments?.getString("SELECTED_DATE") ?: "Unknown" // Retrieve selectedDate from arguments
        val price = servicePrice ?: "₱350" // Default price if not provided

        // Create a new instance of SummaryRegularPlainFragment with service type, selected date, selected time, and service price
        val summaryFragment = SummaryRegularPlainFragment.newInstance(
            serviceType ?: "Unknown", // Pass serviceType
            date, // Pass selectedDate
            time, // Pass selectedTime
            price, // Pass servicePrice
            selectedImageUri.toString() // Pass the selected image URI as a string
        )

        // Replace the current fragment with SummaryRegularPlainFragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, summaryFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Function to determine the service price based on the service type
    private fun getServicePrice(serviceType: String?): String {
        return when (serviceType) {
            "Regular" -> "₱350"
            "Gel Polish" -> "₱450"
            "Removal" -> "₱250"
            else -> "₱350" // Default price if service type is unknown
        }
    }

    companion object {
        // Request code for gallery intent
        private const val PICK_IMAGE_REQUEST_CODE = 100

        /**
         * Create a new instance of BookingAttachImageFragment with the service type and selected time.
         *
         * @param serviceType The type of service (e.g., "Regular" or "Gel Polish").
         * @param selectedTime The selected time (e.g., "9:00 AM").
         * @return A new instance of BookingAttachImageFragment.
         */
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