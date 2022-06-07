package com.example.sleep

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sleep.db.Article
import com.example.sleep.newsapi.NewsApiArticle
import com.example.sleep.newsapi.NewsApiEndpoint
import com.example.sleep.newsapi.NewsApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val articleRepository = ArticleRepository(application)

    private val articles = MutableLiveData<List<Article>>(emptyList())
    fun getArticles(): LiveData<List<Article>> = articles

    init {
        viewModelScope.launch {
            articleRepository.getArticles().collect {
                articles.value = it
            }
        }
    }

    fun addNewArticles(keyword: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                articleRepository.addNewArticles(keyword)
            }
        }
    }
}
