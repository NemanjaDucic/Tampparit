package com.example.tampparit.service
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.example.tampparit.CoreApp.Companion.location
import com.example.tampparit.R
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.LatLongModel
import com.google.android.gms.location.*

class MyForegroundService : Service() {
    private lateinit var windowManager: WindowManager

    private var isRunning = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val NOTIFICATION_ID = 12345
    private val NOTIFICATION_CHANNEL_ID = "MyForegroundServiceChannel"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock
    val locationRequest = LocationRequest.create().apply {
        interval = 1 // Update interval in milliseconds
        fastestInterval = 1 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // High accuracy mode


    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyForegroundService:wakelock")
        handler = Handler()
        runnable = Runnable {
            if (isRunning) {
                updateLocation()
                handler.postDelayed(runnable, 3000) // Run this task every 3 seconds
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

        // Create the notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "MyForegroundServiceChannel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create the notification for the foreground service
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("My Foreground Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0 ?: return
                    val location = p0.lastLocation

                }
            },
            Looper.getMainLooper()
        )
        // Start the foreground service with the notification
        startForeground(NOTIFICATION_ID, notification)
//        wakeLock.acquire()


        // Start the handler to update location every 3 seconds
//        handler.postDelayed(runnable, 3000)
        Thread {
            handler.post(runnable)
        }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(runnable)
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun updateLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (it.result != null){
                update(it.result)
            } else {
                println("stojan")
            }
        }
    }
    private fun update (loca:Location) {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        Instances.databaseInstance.child("drivers").child(sharedPreferences.getString("driverid","").toString()).child("points")
            .child(sharedPreferences.getString("id","").toString()).push().setValue(
                LatLongModel(loca?.latitude, loca?.longitude))
    }
}
