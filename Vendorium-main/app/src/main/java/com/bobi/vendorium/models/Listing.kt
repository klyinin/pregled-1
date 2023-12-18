package com.bobi.vendorium.models

import android.os.Parcel
import android.os.Parcelable

data class Listing(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val ownerID: String = "",
    val dateOfCreation: String = "",
    val category: String = ""
) {
    // Default constructor is added implicitly by Kotlin
}

