package com.example.bookyournailsmobile.NetUtils

import com.example.bookyournailsmobile.Domain.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("login.php") // Replace with your actual login endpoint
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<User>

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