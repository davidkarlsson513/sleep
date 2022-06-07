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

    private val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val articles = MutableLiveData<List<Article>>(emptyList())
    fun getArticles(): LiveData<List<Article>> = articles

    init {
        viewModelScope.launch {
            getApplication<App>().articleDao.getAll().collect {
                articles.value = it
            }
        }
    }

    fun addNewArticles(keyword: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val articles = getNewsApiArticles(keyword)
                articles?.let { addToDatabase(articles) }
            }
        }
    }

    private suspend fun getNewsApiArticles(keyword: String): List<NewsApiArticle>? {
        val pageSize = 100
        var page = 1

        val allArticles = mutableListOf<NewsApiArticle>()

        var done = false
        while (!done) {
            val newsApiResponse = suspendCoroutine<NewsApiResponse?> { continuation ->
                retrofit.create(NewsApiEndpoint::class.java).fetch(keyword, page, pageSize)
                    .enqueue(object : Callback<NewsApiResponse> {
                        override fun onResponse(
                            call: Call<NewsApiResponse>,
                            response: Response<NewsApiResponse>
                        ) {
                            continuation.resume(response.body())
                        }

                        override fun onFailure(call: Call<NewsApiResponse>, t: Throwable) {
                            continuation.resume(null)
                        }
                    })
            }

            if (newsApiResponse == null) {
                Log.w("sleep", "Failed to retrieve articles from NewsApi")
                return null
            }

            allArticles.addAll(newsApiResponse.articles)
            if (allArticles.size == newsApiResponse.totalResults) {
                done = true
            }

            page ++
        }

        return allArticles
    }

    private fun addToDatabase(articles: List<NewsApiArticle>) {
        val dao = getApplication<App>().articleDao
        val dbArticles = articles.map { Article(it.url, it.publishedAt.time, it.title, it.urlToImage, it.content) }
        dao.insert(dbArticles)
    }
}
