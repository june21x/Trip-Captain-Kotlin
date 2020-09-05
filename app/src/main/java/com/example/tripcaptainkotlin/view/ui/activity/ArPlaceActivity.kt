package com.example.tripcaptainkotlin.view.ui.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.ar.PlaceNode
import com.example.tripcaptainkotlin.ar.PlacesArFragment
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.model.getPositionVector
import com.example.tripcaptainkotlin.service.NearbyPlacesService
import com.example.tripcaptainkotlin.viewModel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.ar.sceneform.AnchorNode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class ArPlaceActivity : AppCompatActivity(), SensorEventListener {

    private val REQUEST_CHECK_SETTINGS = 0x1;
    private val TAG = "ArPlaceActivity"

    private lateinit var place: Place

    private lateinit var nearbyPlacesService: NearbyPlacesService
    private lateinit var arFragment: PlacesArFragment
    private lateinit var mapFragment: SupportMapFragment

    // Location
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    // Sensor
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var anchorNode: AnchorNode? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    private lateinit var currentLocation: Location
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isSupportedDevice()) {
            return
        }
        setContentView(R.layout.activity_ar_place)

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        place = intent.getParcelableExtra("Place")!!

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as PlacesArFragment
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.maps_fragment) as SupportMapFragment

        sensorManager = getSystemService()!!
        nearbyPlacesService = NearbyPlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permissions = listOf(
            android.Manifest.permission.CAMERA
        )

        Dexter.withActivity(this@ArPlaceActivity)
            .withPermissions(
                permissions
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(this@ArPlaceActivity, "OK", Toast.LENGTH_SHORT)

                            locationViewModel.getLocationData()
                                .observe(this@ArPlaceActivity, Observer {
                                    currentLocation = it
                                })
                            setUpAr()
                            setUpMaps()

                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(this@ArPlaceActivity, it.name, Toast.LENGTH_SHORT)
            }
            .check()
    }


    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun setUpAr() {
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode(anchor)
            anchorNode?.setParent(arFragment.arSceneView.scene)
            addPlace(anchorNode!!)
        }
    }

    private fun addPlace(anchorNode: AnchorNode) {
        val currentLocation = currentLocation

        // Add the place in AR
        val placeNode =
            PlaceNode(this, arFragment.transformationSystem, true, place, currentLocation)
        placeNode.setParent(anchorNode)
        placeNode.localPosition =
            place.getPositionVector(orientationAngles[0], currentLocation.latLng)

        placeNode.setOnTapListener { _, _ ->
            showInfoWindow(place)
        }

        // Add the place in maps
        map?.let {
            val marker = it.addMarker(
                MarkerOptions()
                    .position(place.geometry.location.latLng)
                    .title(place.name)
            )
            marker.tag = place
            markers.add(marker)
        }

    }

    private fun showInfoWindow(place: Place) {
        // Show in AR
        val matchingPlaceNode = anchorNode?.children?.filter {
            it is PlaceNode
        }?.first {
            val otherPlace = (it as PlaceNode).place ?: return@first false
            return@first otherPlace == place
        } as? PlaceNode
        matchingPlaceNode?.showInfoWindow()

        // Show as marker
        val matchingMarker = markers.firstOrNull {
            val placeTag = (it.tag as? Place) ?: return@firstOrNull false
            return@firstOrNull placeTag == place
        }
        matchingMarker?.showInfoWindow()
    }

//    private fun createLocationRequest() {
//        locationRequest = LocationRequest().apply {
//            interval = 10000
//            fastestInterval = 5000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val client: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener { locationSettingsResponse ->
//            // All location settings are satisfied. The client can initialize
//            // location requests here.
//            setUpMaps()
//        }
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    exception.startResolutionForResult(
//                        this@ArPlaceActivity,
//                        REQUEST_CHECK_SETTINGS
//                    )
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun setUpMaps() {
        mapFragment.getMapAsync { googleMap ->

            googleMap.isMyLocationEnabled = true

            locationViewModel.getLocationData().observe(this@ArPlaceActivity, Observer {
                val pos = CameraPosition.fromLatLngZoom(it.latLng, 16f)
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
            })

            googleMap.setOnMarkerClickListener { marker ->
                val tag = marker.tag
                if (tag !is Place) {
                    return@setOnMarkerClickListener false
                }
                showInfoWindow(tag)
                return@setOnMarkerClickListener true
            }
            map = googleMap
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(TAG, "Could not get location")
        }
    }

    private fun updateCameraBearing(
        googleMap: GoogleMap?,
        bearing: Float
    ) {
        if (googleMap == null) return
        val camPos = CameraPosition
            .builder(
                googleMap.cameraPosition // current Camera
            )
            .bearing(bearing)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

//    private fun getNearbyPlaces(location: Location) {
//        val apiKey = this.getString(R.string.google_maps_key)
//        placesService.nearbyPlaces(
//            apiKey = apiKey,
//            location = "${location.latitude},${location.longitude}",
//            radiusInMeters = 500,
//            placeType = "cafe"
//        ).enqueue(
//            object : Callback<NearbyPlacesResponse> {
//                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
//                    Log.e(TAG, "Failed to get nearby places", t)
//                }
//
//                override fun onResponse(
//                    call: Call<NearbyPlacesResponse>,
//                    response: Response<NearbyPlacesResponse>
//                ) {
//                    if (!response.isSuccessful) {
//                        Log.e(TAG, "Failed to get nearby places")
//                        return
//                    }
//
//                    val places = response.body()?.results ?: emptyList()
//                    this@ArPlaceActivity.places = places
//                }
//            }
//        )
//    }

    private fun isSupportedDevice(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < 3.0) {
            Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            finish()
            return false
        }
        return true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)
