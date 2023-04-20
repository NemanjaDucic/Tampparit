package com.example.tampparit.models

import android.os.Parcel
import android.os.Parcelable

data class HelperModel(
    val map: ArrayList<Map<String, LatLongModel>>?
    ):Parcelable {
    constructor(parcel: Parcel) : this( parcel.readValue(ArrayList::class.java.classLoader) as? ArrayList<Map<String, LatLongModel>>?,) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HelperModel> {
        override fun createFromParcel(parcel: Parcel): HelperModel {
            return HelperModel(parcel)
        }

        override fun newArray(size: Int): Array<HelperModel?> {
            return arrayOfNulls(size)
        }
    }
}