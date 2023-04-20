package com.example.tampparit.models

data class PointModel (
    val id: String ?= "",
    val precisePoints: Map<String, LatLongModel> ?= emptyMap()
        )
