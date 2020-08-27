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

package com.example.tripcaptainkotlin.ar

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.model.Place
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class PlaceNode(
    val context: Context,
    transformationSystem: TransformationSystem,
    var billboarding: Boolean,
    val place: Place?
) : TransformableNode(transformationSystem) {

    private var placeRenderable: ViewRenderable? = null
    private var textViewPlace: TextView? = null

    override fun onActivate() {
        super.onActivate()

        if (scene == null) {
            return
        }

        if (placeRenderable != null) {
            return
        }

        ViewRenderable.builder()
            .setView(context, R.layout.place_view)
            .build()
            .thenAccept { renderable ->
                setRenderable(renderable)
                placeRenderable = renderable
                renderable.isShadowCaster = false
                renderable.isShadowReceiver = false

                place?.let {
                    textViewPlace = renderable.view.findViewById(R.id.placeName)
                    textViewPlace?.text = it.name
                }
            }
    }

    override fun onUpdate(frameTime: FrameTime?) {
        if (billboarding) {
            scene?.let {
                val cameraPosition = it.camera.worldPosition
                val uiPosition: Vector3 = worldPosition
                val direction = Vector3.subtract(cameraPosition, uiPosition)
                direction.y = 0.0f
                val lookRotation =
                    Quaternion.lookRotation(direction, Vector3.up())
                worldRotation = lookRotation
            }
        }
    }


    fun showInfoWindow() {
        // Show text
        textViewPlace?.let {
            it.visibility = if (it.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Hide text for other nodes
        this.parent?.children?.filter {
            it is PlaceNode && it != this
        }?.forEach {
            (it as PlaceNode).textViewPlace?.visibility = View.GONE
        }
    }

}