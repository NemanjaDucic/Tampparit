package com.example.tampparit.models

import android.os.Parcel
import android.os.Parcelable
@kotlinx.serialization.Serializable
data class LatLongModel(
    var latitude : Double ?= 0.0,
    var longitude:Double ?= 0.0
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LatLongModel> {
        override fun createFromParcel(parcel: Parcel): LatLongModel {
            return LatLongModel(parcel)
        }

        override fun newArray(size: Int): Array<LatLongModel?> {
            return arrayOfNulls(size)
        }
    }
}
