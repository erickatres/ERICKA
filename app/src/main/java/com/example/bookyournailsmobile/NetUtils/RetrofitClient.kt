package com.example.bookyournailsmobile.NetUtils

import com.example.bookyournailsmobile.NetUtils.Urls
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = Urls.ROOT + Urls.IP_ADDRESS + Urls.DIRECTORY // Replace with your actual base URL

    val instance: ApiService by lazy {
        // Create a Gson instance with lenient mode enabled
        val gson = GsonBuilder()
            .setLenient() // Enable lenient mode to parse malformed JSON
            .create()

        // Build Retrofit with the lenient Gson converter
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Use the lenient Gson instance
            .build()

        retrofit.create(ApiService::class.java)
    }
}