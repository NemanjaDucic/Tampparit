package com.example.tampparit.service
import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.example.tampparit.CoreApp
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.LatLongModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
class MyBackgroundService : Service() {

    private val timer = Timer()
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun startTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    sendUpdates()
                }
            }
        }, 0, 3000)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun sendUpdates() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
          var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)

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
                Instances.databaseInstance.child(sharedPreferences.getString("driverid","").toString()).child("points")
                    .child(sharedPreferences.getString("id","").toString()).push().setValue(LatLongModel(latitude, longitude))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}