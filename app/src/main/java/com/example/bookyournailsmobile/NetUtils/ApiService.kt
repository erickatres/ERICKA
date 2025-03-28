package com.example.bookyournailsmobile.NetUtils

import com.example.bookyournailsmobile.Domain.User
import com.example.bookyournailsmobile.Models.TimeAvailabilityResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("login2")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("signup") // Replace with your actual registration endpoint
    fun register(@Body registerRequest: RegisterRequest): Call<User>

    @Multipart
    @POST("newbooking") // Replace with your actual endpoint
    fun createBooking(
        @Part("user_id") userId: RequestBody,
        @Part("service_type") serviceType: RequestBody,
        @Part("date") date: RequestBody,
        @Part("time") time: RequestBody,
        @Part("price") price: RequestBody,
        @Part referenceImg: MultipartBody.Part
    ): Call<Void>

    @FormUrlEncoded
    @POST("newbooking") // Replace with your actual endpoint for bookings without an image
    fun createBookingWithoutImage(
        @Field("user_id") userId: String,
        @Field("service_type") serviceType: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("price") price: String
    ): Call<Void>

    @PUT("updatepassword")
    fun changePassword(
        @Body request: ChangePasswordRequest
    ): Call<ChangePasswordResponse>

    @POST("isTimeAlreadyBookedMobile")
    fun checkTimeAvailability(@Body request: TimeAvailabilityRequest): Call<TimeAvailabilityResponse>

    @PUT("resetpassword") // Ensure this matches the backend route
    fun resetPassword(
        @HeaderMap headers: Map<String, String>,
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Call<ResetPasswordResponse>

    @POST("updateuser") // Update this with your actual API endpoint
    fun updateUser(@Body request: UpdateUserRequest): Call<UpdateUserResponse>

    // Add this to your ApiService interface
    @POST("checkemail") // Replace with your actual endpoint
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("checkresetcode") // Replace with your actual endpoint
    fun verifyResetCode(
        @HeaderMap headers: Map<String, String>,
        @Body verifyResetCodeRequest: VerifyResetCodeRequest
    ): Call<VerifyResetCodeResponse>

    @GET("historylist/{user_id}")
    fun getBookingHistoryByUserId(@Path("user_id") userId: String): Call<BookingHistoryResponse>

    @POST("addreview")
    fun submitReview(
        @HeaderMap headers: Map<String, String>,
        @Body reviewRequest: ReviewRequest
    ): Call<Void>

    @GET("reviewlistmobile")
    fun getReviewsByService(@Query("service_type") serviceType: String): Call<ReviewResponse>




    data class BookingRequest(
        val user_id: String,
        val service_type: String,
        val status: String,
        val reference_img: String,
        val price: String,
        val date: String,
        val time: String
    )

    data class ForgotPasswordRequest(
        val email: String
    )

    data class ForgotPasswordResponse(
        val message: String,
        val password_reset_token: String? = null // Optional, depending on your backend response
    )

    data class VerifyOtpRequest(
        val email: String,
        val otp: String,
        val password_reset_token: String
    )

    data class VerifyOtpResponse(
        val status: String,
        val message: String
    )

    data class LoginResponse(
        val message: String,
        val session_token: String,
        val user: User
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class RegisterRequest(
        val first_name: String,
        val last_name: String,
        val email: String,
        val phone: String,
        val password: String,
        val confirm_password: String
    )

    data class VerifyResetCodeRequest(
        val code: String
    )

    data class VerifyResetCodeResponse(
        val message: String,
        val code: String? = null // Optional, depending on your backend response
    )

    data class ResetPasswordRequest(
        val password: String,          // Change from "new_password"
        val confirm_password: String   // Change from "confirm_password"
    )

    data class ResetPasswordResponse(
        val status: String,
        val message: String
    )


    data class BookingHistoryResponse(
        val history: List<BookingHistory>, // Match the backend response
        val count: Int // Optional, if you need the count
    )

    data class BookingHistory(
        val service_type: String,
        val date_formatted: String,
        val date: String, // Optional, if needed
        val status: String // Optional, if needed
    )
    // Add this class to your models
    data class TimeAvailabilityRequest(
        val date: String,
        val time: String
    )
    data class ReviewRequest(
        val user_id: String,
        val service: String,
        val rating: Int,
        val review_text: String
    )
    data class ReviewResponse(
        val count: Int,
        val average_rating: String,
        val reviews: List<Review>
    )

    data class Review(
        val id: Int,
        val service: String,
        val rating: Float,
        val description: String,
        val created_at: String,
        val first_name: String,
        val last_name: String,
        val date_formatted: String
    )
    data class ChangePasswordRequest(
        val old_password: String,
        val password: String,
        val confirm_password: String
    )

    data class ChangePasswordResponse(
        val message: String?,
        val error: String?
    )
    data class UpdateUserRequest(
        val user_id: String,
        val first_name: String?,
        val last_name: String?,
        val email: String?,
        val phone: String?
    )
    data class UpdateUserResponse(
        @SerializedName("message") val message: String
    )
}