package com.example.tripcaptainkotlin.model

import android.webkit.WebView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.tripcaptainkotlin.R
import java.text.SimpleDateFormat
import java.util.*

data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String?,
    val publishedAt: Date,
    val content: String
) {
    fun getPublishedAtToString(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy");
        return formatter.format(publishedAt)
    }

    companion object {
        @BindingAdapter("articleImage")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                Glide.with(view.context)
                    .load(imageUrl)
                    .transition(withCrossFade())
                    .into(view)
            } else {
                Glide.with(view.context)
                    .load(view.context.getString(R.string.no_image_url))
                    .transition(withCrossFade())
                    .into(view)
            }

        }

        @BindingAdapter("loadArticleUrl")
        @JvmStatic
        fun loadWebViewURL(view: WebView, articleUrl: String) {
            view.loadUrl(articleUrl)
        }
    }
}

data class Source(
    val id: String,
    val name: String
)