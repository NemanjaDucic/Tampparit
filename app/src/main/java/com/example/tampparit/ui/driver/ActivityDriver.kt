package com.example.tampparit.ui.driver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.tampparit.CoreApp
import com.example.tampparit.R
import com.example.tampparit.databinding.ActivityDriverLayoutBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.helpers.Instances.gson
import com.example.tampparit.helpers.LocationPermissionHelper
import com.example.tampparit.models.AnnotationModel
import com.example.tampparit.models.DriverModel
import com.example.tampparit.models.LatLongModel
import com.example.tampparit.observers.AppLifecycleObserver
import com.example.tampparit.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class ActivityDriver:AppCompatActivity(), LocationListener {
    private lateinit var binding:ActivityDriverLayoutBinding
    private lateinit var appLifecycleObserver: AppLifecycleObserver

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel:MainViewModel
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
    private var driver = DriverModel()
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    private lateinit var mapView: MapView
    private var lat :Double = 0.0
    private var long :Double = 0.0
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private var flag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityDriverLayoutBinding.inflate(layoutInflater)
        mapView = binding.mapView
        appLifecycleObserver = AppLifecycleObserver(applicationContext)

        setContentView(binding.root)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getAnnotations()
        getDriver()
        viewModel.aList.observe(this){
                value ->

            for (i in value){
                addAnnotationToMap(i)
            }

        }
        listeners()
    }
    private fun addAnnotationToMap(data: AnnotationModel) {
        bitmapFromDrawableRes(
            this@ActivityDriver,
            R.drawable.poin_1
        )?.let {
            val jsonElement = Gson().toJsonTree(data)

            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(data.longitude!!.toDouble(), data.latitude!!.toDouble()))
                .withIconImage(it)
                .withDraggable(true)
                .withData(jsonElement)
                .withTextField(data.name.toString())

            pointAnnotationManager?.create(pointAnnotationOptions)
            pointAnnotationManager!!.addClickListener(OnPointAnnotationClickListener {


                val intent = Intent(this, ActivityDriverDetails::class.java)
                val bundle = Bundle()
                bundle.putSerializable("cons", Gson().fromJson(it.getData(), AnnotationModel::class.java))

                intent.putExtra("constant",bundle)
                startActivity(intent)
                true
            })
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth * 20, drawable.intrinsicHeight * 20,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }

    }
    private  fun listeners(){
        binding.commentEt.isVisible = false
        binding.saveButton.isVisible = false
        binding.nameEt.isVisible = false
        binding.commentButton.setOnClickListener {
            binding.commentEt.isVisible = true
            binding.saveButton.isVisible = true

            getLocation()

        }
        binding.saveButton.setOnClickListener {
            val random = UUID.randomUUID()

            val annotation = AnnotationModel(driver.username,"driver",lat,long,random.toString(),binding.commentEt.text.toString())
            Instances.databaseInstance.child("annotations").child(random.toString()).setValue(annotation).addOnSuccessListener {
                Toast.makeText(this@ActivityDriver,"Thank you for Your Contribution",Toast.LENGTH_SHORT).show()
                binding.commentEt.isVisible = false
                binding.saveButton.isVisible = false
                binding.commentEt.setText(R.string.empty)

            }
        }
        binding.driveButton.setOnClickListener {
            if (flag == true){
                binding.driveButton.text = "Stop Driving"
                startDriving()

                flag = false
            } else {
                binding.driveButton.text = "Start Driving"
                flag = true
            }
        var temp = ArrayList<LatLongModel>()
            for (i in driver.points!!){
                temp.clear()
                temp.addAll(i.value.values)
                println(temp)
            }
        }
    }
    private fun startDriving(){
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
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    location.latitude.also { lat = it }
                }
                if (location != null) {
                    long = location.longitude
                }
            }

        CoroutineScope(Dispatchers.Main).launch {
            var saidID = UUID.randomUUID()
            delay(1000)
            Instances.databaseInstance
                .child("drivers").child(driver.id!!).child("points").child(saidID.toString()).push().setValue(LatLongModel(lat,long))
            startTimer( 3, saidID = saidID.toString())
        }
    }
    private fun startTimer(start: Long,saidID:String) {

        object : CountDownTimer(start * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {

                getLocation()
                Instances.databaseInstance
                    .child("drivers").child(driver.id!!).child("points").child(saidID).push().setValue(LatLongModel(lat,long)).addOnCompleteListener {
                        CoreApp.driverIDString = driver.id!!
                        CoreApp.randomID = saidID
                        viewModel.getSingleDriverRouts(driver.id!!)
                        viewModel.livedataDriver.observe(this@ActivityDriver){
                                driver ->
                            var temp = kotlin.collections.ArrayList<LatLongModel>()
                            for (i in driver.points!!){
                               temp.clear()
                                temp.addAll(i.value.values)
                                addPolylineAnnotations(temp)

                            }
                        }
                        if (flag == false){
                        startTimer(3,saidID)
                         } else {
                             Toast.makeText(this@ActivityDriver,"Your Shift Is Over",Toast.LENGTH_SHORT).show()
                         }
                    }

            }
        }.start()
    }
    private fun addPolylineAnnotations(point:ArrayList<LatLongModel>){
        val annotationApi = mapView?.annotations
        var tempList = ArrayList<Point>()
        tempList.clear()
       for ( i in point){
           i.latitude?.let { i.longitude?.let { it1 -> Point.fromLngLat(it1, it) } }?.let { tempList.add(it) }
       }

        val polylineAnnotationManager = annotationApi?.createPolylineAnnotationManager()
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(tempList)
            // Style the line that will be added to the map.
            .withLineColor("#FF000000")
            .withLineWidth(3.0)
        polylineAnnotationManager?.create(polylineAnnotationOptions)

    }
    private fun getDriver(){
        val id =   Instances.authInstance.currentUser?.uid
        Instances.databaseInstance.child("drivers").child(id!!).get().addOnSuccessListener {

            val json = gson.toJson(it.value)
            driver = Gson().fromJson(json, DriverModel::class.java)
        }
    }
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        long = location.longitude

    }
    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@ActivityDriver,
                    R.drawable.pin,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@ActivityDriver,
                    R.drawable.pin,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
        lifecycle.removeObserver(appLifecycleObserver)

        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }



}