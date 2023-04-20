package com.example.tampparit.models


data class DriverModel(
    var username:String ?= "",
    var email:String ?= "",
    var id:String ?= "",
    var vehicle:String ?= "",
    var points:Map<String,Map<String,LatLongModel>> ?= emptyMap(),
    var asignedBy:String ?= ""
)
