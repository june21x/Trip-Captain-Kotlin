package com.example.tripcaptainkotlin.view.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.FragmentRecommendationsBinding
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.utility.GpsUtils
import com.example.tripcaptainkotlin.view.adapter.PlacesAdapter
import com.example.tripcaptainkotlin.view.callback.PlaceClickCallback
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity
import com.example.tripcaptainkotlin.viewModel.LocationViewModel
import com.example.tripcaptainkotlin.viewModel.PlaceListViewModel

class RecommendationsFragment() : Fragment() {

    private val TAG = "RecommendationsFragment"

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var placeListViewModel: PlaceListViewModel

    private lateinit var binding: FragmentRecommendationsBinding

    private var isGPSEnabled = false

    private val placesAdapter: PlacesAdapter = PlacesAdapter(object : PlaceClickCallback {
        override fun onClickSavePlace(place: Place) {
            //TODO Save Place to Trip
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        placeListViewModel = ViewModelProvider(
            this,
            PlaceListViewModel((activity as MainActivity).application, null, "cafe")
        ).get(PlaceListViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recommendations, container, false)
        binding.apply {
            rvPlace.adapter = placesAdapter
            mActivity = activity as MainActivity
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        GpsUtils(activity as MainActivity).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@RecommendationsFragment.isGPSEnabled = isGPSEnable
            }
        })
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
//            !isGPSEnabled -> latLong.text = getString(R.string.enable_gps)

            isPermissionsGranted() -> startLocationUpdate()

//            shouldShowRequestPermissionRationale() -> latLong.text = getString(R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            (activity as MainActivity).applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    (activity as MainActivity).applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity as MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            activity as MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    private fun startLocationUpdate() {
        locationViewModel.getLocationData().observe(viewLifecycleOwner, Observer {
            placeListViewModel.loadPlaceList(it, "cafe")
            placeListViewModel.placeListLiveData.observe(viewLifecycleOwner, Observer { places ->
                if (places != null) {
                    binding.isLoading = false
                    placesAdapter.setPlaceList(places)
                }
            })

        })
    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101