package com.example.tripcaptainkotlin.model

data class ArticlesResponse(
    var status: String,
    var totalResults: Int,
    var articles: List<Article>
)