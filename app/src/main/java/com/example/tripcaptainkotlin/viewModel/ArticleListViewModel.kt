package com.example.tripcaptainkotlin.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.model.Article
import com.example.tripcaptainkotlin.repository.NewsRepository
import kotlinx.coroutines.launch

class ArticleListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository.instance
    var articleListLiveData: MutableLiveData<List<Article>> = MutableLiveData()

    init {
        loadArticleList()
    }

    private fun loadArticleList() = viewModelScope.launch { //onCleared() のタイミングでキャンセルされる
        try {
            val request = repository.getArticleList(
                getApplication<Application>().getString(R.string.news_api_country),
                getApplication<Application>().getString(R.string.news_api_keyword),
                getApplication<Application>().getString(R.string.news_api_key)
            )
            if (request.isSuccessful) {
                articleListLiveData.postValue(request.body()?.articles) //データを取得したら、LiveDataを更新

            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

}