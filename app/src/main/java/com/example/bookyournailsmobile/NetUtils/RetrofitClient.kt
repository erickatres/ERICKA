package com.example.bookyournailsmobile.NetUtils
import com.example.bookyournailsmobile.NetUtils.Urls
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = Urls.ROOT + Urls.IP_ADDRESS + Urls.DIRECTORY // Replace with your actual base URL

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}