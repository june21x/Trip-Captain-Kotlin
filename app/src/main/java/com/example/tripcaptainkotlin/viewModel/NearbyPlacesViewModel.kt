package com.example.tripcaptainkotlin.viewModel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.model.NearbyPlacesResponse
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.repository.NearbyPlacesRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyPlacesViewModel(
    var application: Application,
    var location: Location?,
    var placeType: String?
) :
    ViewModelProvider.Factory, ViewModel() {

    private val TAG = "PlaceListViewModel"
    private val repository = NearbyPlacesRepository.instance
    var placeListLiveData: MutableLiveData<List<Place>> = MutableLiveData()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        loadPlaceList(location, placeType)
        return NearbyPlacesViewModel(application, location, placeType) as T
    }

    fun loadPlaceList(location: Location?, placeType: String?) =
        viewModelScope.launch { //onCleared() のタイミングでキャンセルされる
            try {
                val request = repository.getPlaceList(
                    application.getString(R.string.google_maps_key),
                    "${location?.latitude},${location?.longitude}",
                    application.resources.getInteger(R.integer.radius_in_meters),
                    placeType ?: "cafe"
                ).enqueue(object : Callback<NearbyPlacesResponse> {
                    override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                        Log.e(TAG, "Failed to get nearby places", t)
                    }

                    override fun onResponse(
                        call: Call<NearbyPlacesResponse>,
                        response: Response<NearbyPlacesResponse>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e(TAG, "Failed to get nearby places")
                            return
                        }

                        placeListLiveData.postValue(response.body()?.results) //データを取得したら、LiveDataを更新
                    }
                })
            } catch (e: Exception) {
                e.stackTrace
            }
        }


}