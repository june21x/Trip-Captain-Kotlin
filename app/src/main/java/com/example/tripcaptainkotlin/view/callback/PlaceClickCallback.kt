package com.example.tripcaptainkotlin.view.callback

import com.example.tripcaptainkotlin.model.Place

interface PlaceClickCallback {
    fun onClickSavePlace(place: Place)
}