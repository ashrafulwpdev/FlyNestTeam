package com.group.FlyNest.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Flight(
    val airline: String,
    val flightNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: Int,
    val stops: Int,
    @DrawableRes val airlineLogo: Int
) : Parcelable