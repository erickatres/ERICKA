package com.example.bookyournailsmobile.Class

import com.google.gson.annotations.SerializedName

data class TimeAvailabilityRequest(
    val date: String,
    val time: String
)
data class TimeAvailabilityResponse(
    @SerializedName("isAvailable") val isAvailable: Boolean,
    @SerializedName("message") val message: String? = null
)