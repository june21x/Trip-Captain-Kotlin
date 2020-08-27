// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.tripcaptainkotlin.model

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.tripcaptainkotlin.R
import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.math.Vector3
import com.google.maps.android.ktx.utils.sphericalHeading
import kotlin.math.cos
import kotlin.math.sin

/**
 * A model describing details about a Place (location, name, type, etc.).
 */
data class Place(
    val id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry,
    val photos: List<PlacePhoto>?,
    val opening_hours: OpeningHours,
    val rating: Float
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Place) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    companion object {
        @BindingAdapter("placePhoto")
        @JvmStatic
        fun loadImage(view: ImageView, photoRef: String?) {
            if (photoRef != null) {
                Glide.with(view.context)
                    .load(getPhotoURL(view.context, photoRef))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            } else {
                Glide.with(view.context)
                    .load(view.context.getString(R.string.no_image_url))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            }

        }

        fun getPhotoURL(context: Context, photoRef: String): String? {
            val photoBaseURL = "https://maps.googleapis.com/maps/api/place/photo?"
            val APIkey: String = context.getString(R.string.google_maps_key)
            val maxHeight = "1600"
            return (photoBaseURL
                    + "maxheight=" + maxHeight
                    + "&photoreference=" + photoRef
                    + "&key=" + APIkey)
        }
    }

}

fun Place.getPositionVector(azimuth: Float, latLng: LatLng): Vector3 {
    val placeLatLng = this.geometry.location.latLng
    val heading = latLng.sphericalHeading(placeLatLng)
    val r = -2f
    val x = r * sin(azimuth + heading).toFloat()
    val y = 1f
    val z = r * cos(azimuth + heading).toFloat()
    return Vector3(x, y, z)
}

data class Geometry(
    val location: GeometryLocation
)

data class GeometryLocation(
    val lat: Double,
    val lng: Double
) {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}

data class PlacePhoto(
    val height: Int,
    val html_attributions: List<String>?,
    val photo_reference: String?,
    val width: Int
)

data class OpeningHours(
    val open_now: Boolean
)