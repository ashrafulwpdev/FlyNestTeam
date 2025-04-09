package com.group.FlyNest.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passenger(
    val name: String,
    val email: String,
    val phone: String,
    val passport: String,
    val isPrimary: Boolean = true,
    val specialRequests: String? = null
) : Parcelable