package com.example.bookyournailsmobile.Models

import com.example.bookyournailsmobile.NetUtils.ApiService

data class Booking(
    val booking_id: Int,
    val service_type: String,
    val date_formatted: String,
    val time: String,
    val status: String,
    val is_reviewed: Int = 0

    // Add this field
)
data class TimeAvailabilityResponse(
    val isAvailable: Boolean,
    val message: String? = null
)
