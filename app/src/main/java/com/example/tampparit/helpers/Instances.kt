package com.example.tampparit.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

object Instances {
    val authInstance = FirebaseAuth.getInstance()
   val databaseInstance: DatabaseReference = FirebaseDatabase.getInstance().reference
    val type:String = "type"
    val gson = Gson()
}