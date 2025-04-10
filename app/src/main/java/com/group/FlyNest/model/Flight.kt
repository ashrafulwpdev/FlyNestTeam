package com.group.FlyNest.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Flight(
    val airline: String = "",
    val flightNumber: String = "",
    val departureAirport: String = "",
    val arrivalAirport: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val flightDate: String = "",
    val price: Int = 0,
    val duration: Int = 0, // in minutes
    val airlineLogo: Int = 0, // resource ID
    val stops: Int = 0 // Added: number of stops
) : Parcelable