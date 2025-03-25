package com.example.bookyournailsmobile.Models

data class Booking(
    val service_type: String,
    val date_formatted: String,
    val status: String // Add this field
)
data class TimeAvailabilityResponse(
    val isAvailable: Boolean,
    val message: String? = null
)
