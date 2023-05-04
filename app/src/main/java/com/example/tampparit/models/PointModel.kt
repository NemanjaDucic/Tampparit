package com.example.tampparit.models

import android.os.Parcel
import android.os.Parcelable

@kotlinx.serialization.Serializable
data class PointModel (
    val id: String ?= "",
    val precisePoints: Map<String,LatLongModel> ?= emptyMap()
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        TODO("precisePoints")
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<PointModel> {
        override fun createFromParcel(parcel: Parcel): PointModel {
            return PointModel(parcel)
        }

        override fun newArray(size: Int): Array<PointModel?> {
            return arrayOfNulls(size)
        }
    }
}
