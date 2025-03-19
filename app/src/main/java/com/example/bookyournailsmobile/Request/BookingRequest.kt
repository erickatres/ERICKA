package com.example.bookyournailsmobile.Request

data class BookingRequest(
    val serviceType: String,
    val status: String,
    val selectedDate: String,
    val selectedTime: String,
    val servicePrice: String,
    val referenceImageUri: String
)