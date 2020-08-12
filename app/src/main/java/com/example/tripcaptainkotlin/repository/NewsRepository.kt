package com.example.tripcaptainkotlin.repository

import com.example.tripcaptainkotlin.model.News
import com.example.tripcaptainkotlin.service.NewsService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HTTPS_NEWS_API_V2_URL = "https://newsapi.org/v2/"

/**
 * ViewModelに対するデータプロバイダ
 */
class NewsRepository {

    companion object Factory {
        val instance: NewsRepository
            @Synchronized get() {
                return NewsRepository()
            }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(HTTPS_NEWS_API_V2_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val newsService: NewsService = retrofit.create(
        NewsService::class.java
    )

    suspend fun getArticleList(country: String, keyword: String, apiKey: String): Response<News> =
        newsService.getArticleList(country, keyword, apiKey)

}
