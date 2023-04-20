package com.example.tampparit.models

data class CityModel(
    var city:String ?= "",
    var lat:String ?= "",
    var lng:String ?= "",
    var country:String ?= "",
    var  iso2 : String ?= "",
    var admin_name : String ?= "",
    var capital : String ?= "",
    var population: String ?= "",
    var population_proper: String ?= ""
)
