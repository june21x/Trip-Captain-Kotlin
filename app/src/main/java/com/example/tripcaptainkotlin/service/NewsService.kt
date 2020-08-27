package com.example.tripcaptainkotlin.service

import com.example.tripcaptainkotlin.model.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    //一覧
    @GET("top-headlines")
    suspend fun getArticleList(
        @Query("country") country: String,
        @Query("keyword") keyword: String,
        @Query("apiKey") apiKey: String
    ): Response<ArticlesResponse>
}