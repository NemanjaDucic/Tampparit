package com.example.tampparit.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.example.tampparit.R
import com.example.tampparit.databinding.ActivityMapBinding
import com.example.tampparit.databinding.AnnotationViewBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.helpers.LocationPermissionHelper
import com.example.tampparit.models.AnnotationModel
import com.example.tampparit.models.LatLongModel
import com.example.tampparit.models.PointModel
import com.example.tampparit.viewmodel.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class ActivityMap:AppCompatActivity(), LocationListener {
    private lateinit var binding:ActivityMapBinding
    private lateinit var locationManager: LocationManager
    var mapView: MapView? = null
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView?.getMapboxMap()?.setCamera(CameraOptions.Builder().center(it).build())
        mapView?.gestures?.focalPoint = mapView?.getMapboxMap()?.pixelForCoordinate(it)
    }
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView?.getMapboxMap()?.setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private lateinit var viewModel: MainViewModel

    private var lat :Double = 0.0
    private var long :Double = 0.0
    private val locationPermissionCode = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }
    private fun init(){
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getAnnotations()
        mapView = binding.mapView
  addPolilineAnnotations()
        viewAnnotationManager = binding.mapView.viewAnnotationManager
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        viewModel.aList.observe(this){
            value ->

            for (i in value){
                addAnnotationToMap(i)
            }
        }



        binding.nameEt.isVisible = false
        binding.commentEt.isVisible = false
        binding.saveButton.isVisible = false
        binding.signinButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.commentButton.setOnClickListener {
            binding.nameEt.isVisible = true
            binding.commentEt.isVisible = true
            binding.saveButton.isVisible = true
            getLocation()

        }
        binding.saveButton.setOnClickListener {
            val random = UUID.randomUUID()
            val annotation = AnnotationModel(binding.nameEt.text.toString(),"user",lat,long,random.toString(),binding.commentEt.text.toString())
            Instances.databaseInstance.child("annotations").child(random.toString()).setValue(annotation).addOnSuccessListener {
                Toast.makeText(this@ActivityMap,"Thank you for Your Contribution",Toast.LENGTH_SHORT).show()
                binding.nameEt.isVisible = false
                binding.commentEt.isVisible = false
                binding.saveButton.isVisible = false
                binding.commentEt.setText(R.string.empty)
                binding.nameEt.setText(R.string.empty)
            }


        }
        drawDriverRoutes()
    }
    private fun onMapReady() {
        mapView?.getMapboxMap()?.setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()

        }
    }
    private fun initLocationComponent() {
        val locationComponentPlugin = mapView?.location
        locationComponentPlugin?.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@ActivityMap,
                    R.drawable.pin,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@ActivityMap,
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
        locationComponentPlugin?.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin?.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }
    private fun addPolilineAnnotations(){
        val annotationApi = mapView?.annotations
        val points = listOf(
            Point.fromLngLat(17.94, 59.25),
            Point.fromLngLat(18.18, 59.37)
        )
        val polylineAnnotationManager = annotationApi?.createPolylineAnnotationManager(mapView!!)
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(points)
            // Style the line that will be added to the map.
            .withLineColor("#FF000000")
            .withLineWidth(5.0)
        polylineAnnotationManager?.create(polylineAnnotationOptions)

    }
    private fun addAnnotationToMap(data:AnnotationModel) {
        bitmapFromDrawableRes(
            this@ActivityMap,
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


                val intent = Intent(this,ActivityDetails::class.java)
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun drawDriverRoutes() {
        viewModel.getAllDrivers()
        viewModel.diverListLiveData.observe(this){
            drivers ->
            var llArray = kotlin.collections.ArrayList<LatLongModel>()
            val allPoints = mutableListOf<List<PointModel>>()

            for (driver in drivers){

                    for (points in driver.points!!.values){
                        llArray.clear()
                        for (i in points){
                           llArray.add(i.value)

                        }
                        addPolilineAnnotations(llArray)

                    }

               }
            }

        }

    private fun addPolilineAnnotations(point:ArrayList<LatLongModel>){
        val annotationApi = mapView?.annotations
        val points = arrayListOf<Point>()
        for (i in point) {
            val geo = (Point.fromLngLat(i.longitude!!,i.latitude!!))
            points.add(geo)
        }

        val polylineAnnotationManager = annotationApi?.createPolylineAnnotationManager(mapView!!)
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(points)
            // Style the line that will be added to the map.
            .withLineColor("#FF000000")
            .withLineWidth(3.0)
        polylineAnnotationManager?.create(polylineAnnotationOptions)

    }
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

}