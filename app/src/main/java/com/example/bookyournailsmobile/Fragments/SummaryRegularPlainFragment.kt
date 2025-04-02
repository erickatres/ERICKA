package com.example.bookyournailsmobile.Fragments

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
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
    private var selectedShape: String? = null  // ✅ Added missing variable
    private var selectedLength: String? = null // ✅ Added missing variable
    private var imageUri: String? = null

    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMobile: TextView
    private lateinit var imgReference: ImageView
    private lateinit var tvServicePrice: TextView
    private lateinit var totalServicePrice: TextView
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView
    private lateinit var tvServiceDetails: TextView
    private lateinit var photoReference: TextView
    private lateinit var tvShape: TextView
    private lateinit var tvLength: TextView
    private lateinit var tvDetailsShape: LinearLayout
    private lateinit var tvDetailsLength: LinearLayout
    private lateinit var tvLoyaltyPoints: TextView
    private lateinit var tvLoyaltyPoints30: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceType = it.getString("SERVICE_TYPE")
            selectedDate = it.getString("SELECTED_DATE")
            selectedTime = it.getString("SELECTED_TIME")
            servicePrice = it.getString("SERVICE_PRICE")
            selectedShape = it.getString("SELECTED_SHAPE") // ✅ Read shape
            selectedLength = it.getString("SELECTED_LENGTH") // ✅ Read length
            imageUri = it.getString("IMAGE_URI")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.summary_regular_plain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val points = sharedPreferences.getInt("loyalty_points", 0)

        tvLoyaltyPoints = view.findViewById(R.id.discount_loyalty_points)
        tvLoyaltyPoints30 = view.findViewById(R.id.discount_30_percent_off)
        tvService = view.findViewById(R.id.tvService)
        btnCancel = view.findViewById(R.id.btnCancel)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tvTime)
        tvMobile = view.findViewById(R.id.tvMobile)
        tvServicePrice = view.findViewById(R.id.tvservicePrice)
        totalServicePrice = view.findViewById(R.id.total_price)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        photoReference = view.findViewById(R.id.tvPhotoReference)
        imgReference = view.findViewById(R.id.imgPhotoReference)
        tvServiceDetails = view.findViewById(R.id.tv_service_details)
        tvShape = view.findViewById(R.id.tvShape)
        tvLength = view.findViewById(R.id.tvLength)
        tvDetailsLength = view.findViewById(R.id.details_length)
        tvDetailsShape = view.findViewById(R.id.details_shape)

        val originalPrice = servicePrice?.toDoubleOrNull()?.toInt() ?: 0
        val user = (activity as? MainActivity)?.getUserFromPreferences()
        user?.let { u ->
            tvMobile.text = u.phone
        }
        tvService.text = serviceType ?: "N/A"
        tvDate.text = selectedDate ?: "N/A"
        tvTime.text = selectedTime ?: "N/A"
        tvServicePrice.text = servicePrice?.let { "₱$it" } ?: "N/A"
        totalServicePrice.text = servicePrice?.let { "₱$it" } ?: "N/A"
        tvServiceDetails.text = serviceType ?: "Null"
        tvShape.text = selectedShape ?: "Null"
        tvLength.text = selectedLength ?: "Null"

        if (points >= 100) {
            // Apply 30% discount and convert to integer
            val discountedPrice = (originalPrice * 0.7).toInt()
            totalServicePrice.text = "₱$discountedPrice"

            // Show discount labels
            tvLoyaltyPoints.visibility = View.VISIBLE
            tvLoyaltyPoints30.visibility = View.VISIBLE
        } else {
            // Keep the original price as an integer
            totalServicePrice.text = "₱$originalPrice"

            // Hide discount labels
            tvLoyaltyPoints.visibility = View.GONE
            tvLoyaltyPoints30.visibility = View.GONE
        }

        if (serviceType == "Removal") {
            imgReference.visibility = View.GONE
            photoReference.visibility = View.GONE
        } else {
            imageUri?.let {
                val uri = Uri.parse(it)
                imgReference.visibility = View.VISIBLE
                photoReference.visibility = View.VISIBLE
                Glide.with(this).load(uri).into(imgReference)
            }
        }
        if (serviceType == "Soft Gel X") {
            tvShape.visibility = View.VISIBLE
            tvLength.visibility = View.VISIBLE
            tvDetailsShape.visibility = View.VISIBLE
            tvDetailsLength.visibility = View.VISIBLE
            tvShape.text = selectedShape ?: "N/A"
            tvLength.text = selectedLength ?: "N/A"
        } else {
            tvShape.visibility = View.GONE
            tvLength.visibility = View.GONE
            tvDetailsShape.visibility = View.GONE
            tvDetailsLength.visibility = View.GONE
        }

        btnConfirm.setOnClickListener {
            user?.let { userObj ->
                uploadBookingToServer(userObj)
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "User not found. Please log in.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnCancel.setOnClickListener {
            showCancelPopup()
        }
    }



    private fun showCancelPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.pop_up_cancel)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the width of the dialog when it is shown
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust width here
        }

        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnYes.setOnClickListener {
            dialog.dismiss()
            navigateToHomeFragment()
        }

        dialog.show()
    }



    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, homeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    private fun showSuccessPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.success_booking)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the width of the dialog when it is shown
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(900, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust width here
        }

        val btnOk = dialog.findViewById<TextView>(R.id.btn_see_booking)
        btnOk.setOnClickListener {
            dialog.dismiss()
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

    override fun onResume() {
        super.onResume()

        (activity as? MainActivity)?.setBottomNavVisibility(false) // Hide bottom nav

        // Disable back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable back button
            }
        })
    }


    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisibility(true) // Show bottom nav again when leaving
    }

    private fun uploadBookingToServer(user: User) {
        val context = requireContext()

        // Ensure all required fields are present
        val userId = user.getId() ?: run {
            Toast.makeText(context, "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val serviceType = serviceType ?: run {
            Toast.makeText(context, "Service type is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedDate = selectedDate ?: run {
            Toast.makeText(context, "Date is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedTime = selectedTime ?: run {
            Toast.makeText(context, "Time is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val servicePrice = servicePrice ?: run {
            Toast.makeText(context, "Price is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.create(context)

        // ✅ Ensure imagePart is always non-null
        val imagePart = if (imageUri != null) {
            val uri = Uri.parse(imageUri)
            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                Toast.makeText(context, "Failed to open image file", Toast.LENGTH_SHORT).show()
                return
            }

            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("reference_img", tempFile.name, requestFile)
        } else {
            // ✅ If no image is provided, send an empty file
            val emptyFile = File.createTempFile("empty", ".jpg", context.cacheDir)
            val emptyRequestFile = emptyFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("reference_img", emptyFile.name, emptyRequestFile)
        }

        when (serviceType) {
            "Soft Gel X" -> {
                val selectedShape = selectedShape ?: run {
                    Toast.makeText(context, "Shape is missing", Toast.LENGTH_SHORT).show()
                    return
                }
                val selectedLength = selectedLength ?: run {
                    Toast.makeText(context, "Length is missing", Toast.LENGTH_SHORT).show()
                    return
                }
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val serviceTypePart = serviceType.toRequestBody("text/plain".toMediaTypeOrNull())
                val datePart = selectedDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val timePart = selectedTime.toRequestBody("text/plain".toMediaTypeOrNull())
                val pricePart = servicePrice.toRequestBody("text/plain".toMediaTypeOrNull())
                val shapePart = selectedShape.toRequestBody("text/plain".toMediaTypeOrNull())
                val lengthPart = selectedLength.toRequestBody("text/plain".toMediaTypeOrNull())


                val call = apiService.createSoftGelXBooking(
                    userIdPart, serviceTypePart, datePart, timePart, pricePart, shapePart, lengthPart, imagePart
                )

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            showSuccessPopup()
                        } else {
                            Toast.makeText(context, "Failed to create booking: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            "Removal" -> {
                val call = apiService.createBookingWithoutImage(
                    userId, serviceType, selectedDate, selectedTime, servicePrice
                )

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            showSuccessPopup()
                        } else {
                            Toast.makeText(context, "Failed to create booking: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            else -> {
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val serviceTypePart = serviceType.toRequestBody("text/plain".toMediaTypeOrNull())
                val datePart = selectedDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val timePart = selectedTime.toRequestBody("text/plain".toMediaTypeOrNull())
                val pricePart = servicePrice.toRequestBody("text/plain".toMediaTypeOrNull())

                val call = apiService.createBooking(
                    userIdPart, serviceTypePart, datePart, timePart, pricePart, imagePart
                )

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            showSuccessPopup()
                        } else {
                            Toast.makeText(context, "Failed to create booking: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            serviceType: String,
            selectedDate: String,
            selectedTime: String,
            servicePrice: String,
            selectedShape: String, // ✅ Add this
            selectedLength: String, // ✅ Add this
            imageUri: String
        ) = SummaryRegularPlainFragment().apply {
            arguments = Bundle().apply {
                putString("SERVICE_TYPE", serviceType)
                putString("SELECTED_DATE", selectedDate)
                putString("SELECTED_TIME", selectedTime)
                putString("SERVICE_PRICE", servicePrice)
                putString("SELECTED_SHAPE", selectedShape) // ✅ Store shape
                putString("SELECTED_LENGTH", selectedLength) // ✅ Store length
                putString("IMAGE_URI", imageUri)
            }
        }
    }
}