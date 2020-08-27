package com.example.tripcaptainkotlin.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tripcaptainkotlin.model.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}