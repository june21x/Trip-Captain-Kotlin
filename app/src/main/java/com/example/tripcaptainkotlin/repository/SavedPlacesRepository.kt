package com.example.tripcaptainkotlin.repository

import android.app.Application
import com.example.tripcaptainkotlin.model.Place
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SavedPlacesRepository {
    private val TAG = "SAVED_PLACES_REPOSITORY"
    private var firestoreDB = FirebaseFirestore.getInstance()

    // save address to firebase
    fun savePlace(phoneNumber: String, place: Place): Task<Void> {
        var documentReference = firestoreDB.collection("saved").document(phoneNumber)
            .collection("places").document(place.place_id)
        return documentReference.set(place)
    }

    // get saved addresses from firebase
    fun getSavedPlaces(application: Application, phoneNumber: String): CollectionReference {
        var collectionReference = firestoreDB.collection("saved/${phoneNumber}/places")
        return collectionReference
    }

    fun deletePlaces(phoneNumber: String, place: Place): Task<Void> {
        var documentReference = firestoreDB.collection("saved/${phoneNumber}/places")
            .document(place.place_id)

        return documentReference.delete()
    }
}