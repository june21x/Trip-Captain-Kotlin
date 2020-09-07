package com.example.tripcaptainkotlin.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.repository.SavedPlacesRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class SavedPlacesViewModel() : ViewModel() {
    private val TAG = "SAVED_PLACES_VIEW_MODEL"
    private var savedPlacesRepository = SavedPlacesRepository()
    private var savedPlaces: MutableLiveData<List<Place>> = MutableLiveData()

    // save place to firebase
    fun savePlaceToFirebase(phoneNumber: String, place: Place) {
        place.setLastSavedWithCurrentDate()
        savedPlacesRepository.savePlace(phoneNumber, place).addOnFailureListener {
            Log.e(TAG, "Failed to save Place!")
        }
    }

    // get realtime updates from firebase regarding saved places
    fun getSavedPlaces(application: Application, phoneNumber: String): LiveData<List<Place>> {
        viewModelScope.launch {
            savedPlacesRepository.getSavedPlaces(application, phoneNumber)
                .orderBy("last_saved", Query.Direction.DESCENDING).addSnapshotListener(
                    EventListener<QuerySnapshot> { value, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            savedPlaces.value = null
                            return@EventListener
                        }

                        val placeList: MutableList<Place> = mutableListOf()
                        for (doc in value!!) {
                            val place = doc.toObject(Place::class.java)

                            placeList.add(place)
                            savedPlaces.postValue(placeList)
                            Log.d(TAG, "${savedPlaces.value?.size}")
                        }

                    })
        }

        return savedPlaces
    }

    // delete a place from firebase
    fun deletePlace(phoneNumber: String, place: Place) {
        savedPlacesRepository.deletePlaces(phoneNumber, place).addOnFailureListener {
            Log.e(TAG, "Failed to delete Place")
        }
    }
}