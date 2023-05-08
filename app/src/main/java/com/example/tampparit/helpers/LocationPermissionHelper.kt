package com.example.tampparit.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import java.lang.ref.WeakReference

class LocationPermissionHelper(val activity: WeakReference<Activity>) {
    private lateinit var permissionsManager: PermissionsManager

    fun checkPermissions(onMapReady: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(activity.get()) &&
            ContextCompat.checkSelfPermission(activity.get()!!, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
        ) {
            onMapReady()
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    val msg = StringBuilder("You need to accept the following permissions:")
                    permissionsToExplain.forEach {
                        msg.append("\n")
                        msg.append(it)
                    }
                    Toast.makeText(
                        activity.get(), msg.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        onMapReady()
                    } else {
                        activity.get()?.finish()
                    }
                }
            })
            permissionsManager.requestLocationPermissions(activity.get())
            ActivityCompat.requestPermissions(
                activity.get()!!,
                arrayOf(Manifest.permission.WAKE_LOCK),
                WAKE_LOCK_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == WAKE_LOCK_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions {}
            } else {
                activity.get()?.finish()
            }
        } else {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        const val WAKE_LOCK_PERMISSION_REQUEST_CODE = 1
    }
}

