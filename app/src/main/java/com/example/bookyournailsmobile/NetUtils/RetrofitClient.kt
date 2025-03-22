package com.example.bookyournailsmobile.NetUtils

import android.content.Context
import com.example.bookyournailsmobile.Managers.SessionManagement
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = Urls.ROOT // Replace with your actual base URL

    // Function to create a Retrofit instance with the session token
    fun create(context: Context): ApiService {
        val sessionManagement = SessionManagement(context)
        val sessionToken = sessionManagement.getSessionToken()

        // Create an OkHttpClient with an interceptor to add the Authorization header
        val client = OkHttpClient.Builder().apply {
            if (sessionToken != null) {
                addInterceptor(Interceptor { chain ->
                    val originalRequest = chain.request()
                    val requestWithToken = originalRequest.newBuilder()
                        .header("Authorization", sessionToken)
                        .build()
                    chain.proceed(requestWithToken)
                })
            }
        }.build()

        // Create a Gson instance with lenient mode enabled
        val gson = GsonBuilder()
            .setLenient() // Enable lenient mode to parse malformed JSON
            .create()

        // Build Retrofit with the lenient Gson converter and the custom OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}