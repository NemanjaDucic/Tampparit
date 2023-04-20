package com.example.tampparit.models

data class AnnotationModel(
    val name:String ?= "",
    val type:String ?= "",
    val latitude:Double ?= 0.0,
    val longitude:Double ?= 0.0,
    val uid:String ?= "",
    val comment:String ?= ""

):java.io.Serializable
