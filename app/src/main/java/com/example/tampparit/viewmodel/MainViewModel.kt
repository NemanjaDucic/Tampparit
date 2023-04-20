package com.example.tampparit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tampparit.adapters.AdminListAdapter
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MainViewModel:ViewModel() {
private val gson = Gson()
    private var listOfA = ArrayList<AnnotationModel>()
    private var _aList = MutableLiveData<ArrayList<AnnotationModel>>()
    var aList = _aList as LiveData<ArrayList<AnnotationModel>>

    var user = AdminModel()

    private var listOfDriverPoints = ArrayList<LatLongModel>()
    private var _driverPointsLiveData = MutableLiveData<ArrayList<LatLongModel>>()
    var driverPointsLiveData = _driverPointsLiveData as LiveData<ArrayList<LatLongModel>>

    private var driverList = ArrayList<DriverModel>()
    private var _driveRListLiveData = MutableLiveData<ArrayList<DriverModel>>()
    var diverListLiveData = _driveRListLiveData as LiveData<ArrayList<DriverModel>>

    private var adminList = ArrayList<AdminModel>()
    private var _adminListLiveData = MutableLiveData<ArrayList<AdminModel>>()
    var adminListLiveData = _adminListLiveData as LiveData<ArrayList<AdminModel>>

    fun getAnnotations() {
        Instances.databaseInstance.child("annotations")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listOfA.clear()
                    for (l in dataSnapshot.children) {
                        val json = gson.toJson(l.value)
                        val data = Gson().fromJson(json, AnnotationModel::class.java)
                        listOfA.add(data)
                    }
                    _aList.postValue(listOfA)
                }

                override fun onCancelled(error: DatabaseError) {

                    println("Error")
                }
            })
    }
        fun getAllDrivers() {
            Instances.databaseInstance.child("drivers")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        driverList.clear()
                        for (l in dataSnapshot.children) {
                            val json = gson.toJson(l.value)
                            val data = Gson().fromJson(json, DriverModel::class.java)
                            driverList.add(data)


                        }
                        _driveRListLiveData.postValue(driverList)

                    }

                    override fun onCancelled(error: DatabaseError) {

                        println("Error")
                    }
                })
        }
    fun getSingleDriverRouts(driverID:String) {
        Instances.databaseInstance.child("drivers").child(driverID).child("points")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listOfDriverPoints.clear()
                    for (l in dataSnapshot.children) {
                        val json = gson.toJson(l.value)
                        println(l)
                        val data = Gson().fromJson(json, HelperModel::class.java)
//                        listOfDriverPoints.add(data)
                        println(data)
                        println(json)
                    }
//                    _driverPointsLiveData.postValue(listOfDriverPoints)
                }

                override fun onCancelled(error: DatabaseError) {

                    println("Error")
                }
            })
    }
    fun getAdmins(){
        Instances.databaseInstance.child("admins")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    adminList.clear()
                    for (l in dataSnapshot.children) {
                        val json = gson.toJson(l.value)
                        val data = Gson().fromJson(json, AdminModel::class.java)
                        adminList.add(data)
                    }
                    _adminListLiveData.postValue(adminList)
                }

                override fun onCancelled(error: DatabaseError) {

                    println("Error")
                }
            })
    }
    fun deleteAdmin(admin:String){
        Instances.databaseInstance.child("admins").child(admin).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
            }
            else {
                println("error")
            }
        }
    }
    fun getAdminByID(id:String){
        Instances.databaseInstance.child("admins").child(id).get().addOnCompleteListener {
            if (it.isSuccessful){
                val json = gson.toJson(it.result.value)
                val data = Gson().fromJson(json, AdminModel::class.java)
                user = data
            }
        }
    }
}
