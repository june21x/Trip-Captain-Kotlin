package com.example.tripcaptainkotlin.repository

import com.example.tripcaptainkotlin.model.NearbyPlacesResponse
import com.example.tripcaptainkotlin.service.NearbyPlacesService
import retrofit2.Call


/**
 * ViewModelに対するデータプロバイダ
 */
class NearbyPlacesRepository {

    companion object Factory {
        val instance: NearbyPlacesRepository
            @Synchronized get() {
                return NearbyPlacesRepository()
            }
    }

    private val nearbyPlacesService: NearbyPlacesService = NearbyPlacesService.create()

    fun getPlaceList(
        key: String,
        location: String,
        radiusInMeters: Int,
        placeType: String
    ): Call<NearbyPlacesResponse> =
        nearbyPlacesService.nearbyPlaces(key, location, radiusInMeters, placeType)

}
