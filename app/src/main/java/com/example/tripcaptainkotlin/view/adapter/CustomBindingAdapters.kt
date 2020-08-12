package com.example.tripcaptainkotlin.view.adapter

import android.view.View
import androidx.databinding.BindingAdapter

object CustomBindingAdapters {
    @BindingAdapter("app:visibleGone")
    @JvmStatic
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}
