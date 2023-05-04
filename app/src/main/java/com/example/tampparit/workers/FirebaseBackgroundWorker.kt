package com.example.tampparit.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tampparit.CoreApp
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.LatLongModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class FirebaseBackgroundWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private  var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    override fun doWork(): Result {
        updateLocation()
        return Result.success()
    }
    private fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations, this can be null.
            if (location != null) {
                // Do something with the location
                val latitude = location.latitude
                val longitude = location.longitude
                Instances.databaseInstance.child(CoreApp.driverIDString).child("points")
                    .child(CoreApp.randomID).push().setValue(LatLongModel(latitude, longitude))
            }
        }

    }
}