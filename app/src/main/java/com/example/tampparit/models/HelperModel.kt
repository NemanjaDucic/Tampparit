package com.example.tampparit.models

import android.os.Parcel
import android.os.Parcelable

@kotlinx.serialization.Serializable
data class HelperModel(
    val map: Map<String,Map<String,LatLongModel>>
    ):Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}