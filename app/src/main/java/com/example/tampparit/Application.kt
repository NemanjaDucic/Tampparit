package com.example.tampparit

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.tampparit.models.LatLongModel
import com.example.tampparit.observers.AppLifecycleObserver
import com.example.tampparit.ui.ActivityMap

open class CoreApp: Application() {
    companion object {
        var driverIDString: String = ""
        var randomID:String = ""
        var location:LatLongModel = LatLongModel()
    }
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(applicationContext))

    }
}