package com.example.tripcaptainkotlin.repository

import com.example.tripcaptainkotlin.model.NearbyPlacesResponse
import com.example.tripcaptainkotlin.service.PlacesService
import retrofit2.Call


/**
 * ViewModelに対するデータプロバイダ
 */
class PlacesRepository {

    companion object Factory {
        val instance: PlacesRepository
            @Synchronized get() {
                return PlacesRepository()
            }
    }

    private val placesService: PlacesService = PlacesService.create()

    fun getPlaceList(
        key: String,
        location: String,
        radiusInMeters: Int,
        placeType: String
    ): Call<NearbyPlacesResponse> =
        placesService.nearbyPlaces(key, location, radiusInMeters, placeType)

}
