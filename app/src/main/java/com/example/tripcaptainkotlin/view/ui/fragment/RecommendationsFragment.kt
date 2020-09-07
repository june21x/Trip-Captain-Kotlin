package com.example.tripcaptainkotlin.view.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.FragmentRecommendationsBinding
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.utility.GpsUtils
import com.example.tripcaptainkotlin.view.adapter.NearbyPlacesAdapter
import com.example.tripcaptainkotlin.view.ui.activity.ArPlaceActivity
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity
import com.example.tripcaptainkotlin.viewModel.LocationViewModel
import com.example.tripcaptainkotlin.viewModel.NearbyPlacesViewModel
import com.example.tripcaptainkotlin.viewModel.SavedPlacesViewModel
import kotlinx.android.synthetic.main.fragment_recommendations.*
import kotlinx.android.synthetic.main.layout_place_type_selection.view.*

class RecommendationsFragment : Fragment() {

    private val TAG = "RecommendationsFragment"

    private var isGPSEnabled = false
    private var checkedId = R.id.rbCafe
    private lateinit var currentLocation: Location
    private var placeType = "cafe"

    private val locationViewModel by lazy {
        ViewModelProvider(this@RecommendationsFragment).get(LocationViewModel::class.java)
    }

    private val nearbyPlacesViewModel by lazy {
        ViewModelProvider(
            this@RecommendationsFragment,
            NearbyPlacesViewModel((activity as MainActivity).application, null, placeType)
        ).get(NearbyPlacesViewModel::class.java)
    }

    private val savedPlacesViewModel by lazy {
        ViewModelProvider(this@RecommendationsFragment).get(SavedPlacesViewModel::class.java)
    }

    private lateinit var binding: FragmentRecommendationsBinding

    private lateinit var phoneNumber: String

    private val nearbyPlacesAdapter: NearbyPlacesAdapter =
        NearbyPlacesAdapter(this@RecommendationsFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val sharedPref =
            (activity as MainActivity).getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
                ?: return
        val defaultValue = "0174270608"
        phoneNumber = sharedPref.getString(getString(R.string.phone_number), defaultValue)!!





        startLocationUpdate(placeType)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recommendations, container, false)
        binding.apply {
            rvPlace.adapter = nearbyPlacesAdapter
            recommendationsFragment = this@RecommendationsFragment
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        GpsUtils(activity as MainActivity).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@RecommendationsFragment.isGPSEnabled = isGPSEnable
                invokeLocationAction()
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

            isPermissionsGranted() -> startLocationUpdate(placeType)

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

    private fun startLocationUpdate(placeType: String) {
        if (isGPSEnabled) {
            locationViewModel.getLocationData().observe(viewLifecycleOwner, Observer {
                currentLocation = it
                nearbyPlacesViewModel.loadPlaceList(currentLocation, placeType)
                nearbyPlacesViewModel.placeListLiveData.observe(
                    viewLifecycleOwner,
                    Observer { places ->
                        updateRecyclerView(places)
                    })
            })
        }
    }

    fun switchPlaceType() {
        // create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Select a Place Type")
        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.layout_place_type_selection, null)
        customLayout.findViewById<RadioButton>(checkedId).isChecked = true
        builder.setView(customLayout)

        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

        customLayout.rgPlaceType.setOnCheckedChangeListener { group, checkedId ->
            this.checkedId = checkedId
            val radioButton: RadioButton = group.findViewById(this.checkedId)
            placeType = radioButton.text.toString().toLowerCase().replace(' ', '_')

            startLocationUpdate(placeType)
            dialog.dismiss()
        }
    }

    fun viewPlaceInAR(place: Place) {
        val intent = Intent(activity as MainActivity, ArPlaceActivity::class.java)
        intent.putExtra("Place", place)
        startActivity(
            intent,
            ActivityOptions.makeSceneTransitionAnimation(activity as MainActivity).toBundle()
        )
        onPause()
    }

    fun savePlace(place: Place) {
        val sharedPref =
            (activity as MainActivity).getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
                ?: return
        val defaultValue = ""
        phoneNumber = sharedPref.getString(getString(R.string.phone_number), defaultValue)!!

        if (phoneNumber != "") {
            savedPlacesViewModel.savePlaceToFirebase(phoneNumber, place)
            Toast.makeText(
                context,
                "${place.name} has been saved by ${phoneNumber}.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(activity, "Phone Number is needed to save place", Toast.LENGTH_SHORT)
                .show()
            (activity as MainActivity).editPhoneNumber()
        }


    }

    private fun updateRecyclerView(places: List<Place>) {
        if (places != null) {
            rvPlace.visibility = View.VISIBLE
            llNoResult.visibility = View.GONE
            nearbyPlacesAdapter.setPlaceList(places)

            if (places.isEmpty()) {
                rvPlace.visibility = View.GONE
                llNoResult.visibility = View.VISIBLE
            }
        }
    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101