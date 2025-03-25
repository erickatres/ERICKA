package com.example.bookyournailsmobile.Fragments

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookyournailsmobile.Activities.MainActivity
import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.NetUtils.RetrofitClient
import com.example.bookyournailsmobile.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SummaryRegularPlainFragment : Fragment() {

    private var serviceType: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var servicePrice: String? = null
    private var referenceImageUri: String? = null

    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvServiceDetails: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var totalServicePrice: TextView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceType = arguments?.getString("SERVICE_TYPE")
        selectedDate = arguments?.getString("SELECTED_DATE")
        selectedTime = arguments?.getString("SELECTED_TIME")
        servicePrice = arguments?.getString("SERVICE_PRICE")
        referenceImageUri = arguments?.getString("REFERENCE_IMAGE_URI")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.summary_regular_plain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvService = view.findViewById(R.id.tvService)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tvTime)
        tvMobile = view.findViewById(R.id.tvMobile)
        tvServicePrice = view.findViewById(R.id.tvservicePrice)
        totalServicePrice = view.findViewById(R.id.total_service_price)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        tvServiceDetails = view.findViewById(R.id.service_price_details)

        val user = requireContext().getUserFromPreferences()
        user?.let {
            tvMobile.text = it.phone
        }

        tvService.text = serviceType ?: "N/A"
        tvDate.text = selectedDate ?: "N/A"
        tvTime.text = selectedTime ?: "N/A"
        totalServicePrice.text = servicePrice ?: "N/A"
        tvServicePrice.text = servicePrice ?: "N/A"
        tvServiceDetails.text = serviceType ?: "Null"

        btnConfirm.setOnClickListener {
            user?.let {
                uploadBookingToServer(it)
            } ?: run {
                Toast.makeText(requireContext(), "User not found. Please log in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSuccessPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.success_booking)
        dialog.setCancelable(false)

        // Set the width and height of the dialog
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt() // 85% of screen width
        val height = (resources.displayMetrics.heightPixels * 0.3).toInt() // 50% of screen height
        dialog.window?.setLayout(width, height)

        val btnOk = dialog.findViewById<Button>(R.id.btn_see_booking)
        btnOk.setOnClickListener {
            dialog.dismiss()
            // Navigate to BookingFragment
            navigateToBookingFragment()
        }

        dialog.show()
    }

    private fun navigateToBookingFragment() {
        // Create an instance of BookingFragment
        val bookingFragment = BookingFragment()

        // Begin the transaction
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()

        // Replace the current fragment with BookingFragment
        transaction.replace(R.id.fragment_container, bookingFragment) // Replace `fragment_container` with your container ID

        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }


    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true) // Show bottom nav again when leaving
    }

    private fun uploadBookingToServer(user: User) {
        // Ensure all required fields are present
        val userId = user.getId() ?: run {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val serviceType = serviceType ?: run {
            Toast.makeText(requireContext(), "Service type is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedDate = selectedDate ?: run {
            Toast.makeText(requireContext(), "Date is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedTime = selectedTime ?: run {
            Toast.makeText(requireContext(), "Time is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val servicePrice = servicePrice ?: run {
            Toast.makeText(requireContext(), "Price is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.create(requireContext())

        if (serviceType == "Removal") {
            // Call API without image for "Removal"
            val call = apiService.createBookingWithoutImage(
                userId,
                serviceType,
                selectedDate,
                selectedTime,
                servicePrice
            )

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showSuccessPopup()
                    } else {
                        Toast.makeText(requireContext(), "Failed to create booking: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Ensure reference image is available for non-Removal services
            val referenceImageUri = referenceImageUri ?: run {
                Toast.makeText(requireContext(), "Reference image is missing", Toast.LENGTH_SHORT).show()
                return
            }

            // Convert the URI to a file
            val uri = Uri.parse(referenceImageUri)
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: run {
                Toast.makeText(requireContext(), "Failed to open image file", Toast.LENGTH_SHORT).show()
                return
            }

            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("reference_img", tempFile.name, requestFile)

            val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
            val serviceTypePart = serviceType.toRequestBody("text/plain".toMediaTypeOrNull())
            val datePart = selectedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val timePart = selectedTime.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = servicePrice.toRequestBody("text/plain".toMediaTypeOrNull())

            val call = apiService.createBooking(
                userIdPart,
                serviceTypePart,
                datePart,
                timePart,
                pricePart,
                imagePart
            )

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showSuccessPopup()
                    } else {
                        Toast.makeText(requireContext(), "Failed to create booking: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(
            serviceType: String,
            selectedDate: String,
            selectedTime: String,
            servicePrice: String,
            imageUri: String
        ) = SummaryRegularPlainFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType)
                putString("SELECTED_DATE", selectedDate)
                putString("SELECTED_TIME", selectedTime)
                putString("SERVICE_PRICE", servicePrice)
                putString("REFERENCE_IMAGE_URI", imageUri)
            }
        }
    }
}