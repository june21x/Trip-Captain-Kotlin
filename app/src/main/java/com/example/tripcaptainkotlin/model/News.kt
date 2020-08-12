package com.example.tripcaptainkotlin.model

data class News(
    var status: String,
    var totalResults: Int,
    var articles: List<Article>
)