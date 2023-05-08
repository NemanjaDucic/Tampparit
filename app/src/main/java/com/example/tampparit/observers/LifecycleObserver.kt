package com.example.tampparit.observers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.tampparit.workers.FirebaseBackgroundWorker

class AppLifecycleObserver(private val context:Context) : LifecycleObserver {

    private var fbHandler: Handler? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val updateRequest = OneTimeWorkRequest.Builder(FirebaseBackgroundWorker::class.java)
            .build()
        WorkManager.getInstance().enqueue(updateRequest)
        fbHandler = Handler(Looper.getMainLooper())
        fbHandler?.postDelayed({
            WorkManager.getInstance(context).enqueue(updateRequest)
        }, 3000)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        fbHandler?.removeCallbacksAndMessages(null)
        fbHandler = null
    }

}
