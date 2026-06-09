package com.example.bichimovil.core.network.data

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Represents Firebase Firestore Timestamp format
 * API returns: { "_seconds": 1717325400, "_nanoseconds": 0 }
 */
data class FirebaseTimestamp(
    @SerializedName("_seconds")
    val seconds: Long,

    @SerializedName("_nanoseconds")
    val nanoseconds: Int
) {
    fun toDate(): Date = Date(seconds * 1000)
}