package com.example.bookyournailsmobile.NetUtils

import com.example.bookyournailsmobile.Domain.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("login2")
    fun login(@Body loginRequest: LoginRequest): Call<User>
    @FormUrlEncoded
    @POST("signup.php") // Replace with your actual registration endpoint
    fun register(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<User>

    // Add a new endpoint for creating a booking
    @POST("create_booking.php")
    fun createBooking(@Body bookingData: BookingRequest): Call<Void>

    // Add a new endpoint for OTP verification
    @FormUrlEncoded
    @POST("verify_otp.php")
    fun verifyOtp(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Call<VerifyOtpResponse>
}

data class BookingRequest(
    val user_id: String,
    val service_type: String,
    val status: String,
    val reference_img: String,
    val price: String,
    val date: String,
    val time: String
)
data class LoginRequest(
    val email: String,
    val password: String
)


data class VerifyOtpResponse(
    val status: String,
    val message: String
)