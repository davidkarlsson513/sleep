package com.example.sleep

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.sleep.db.Article
import com.example.sleep.db.ArticleDao
import com.example.sleep.db.ArticleDatabase
import com.example.sleep.newsapi.NewsApiArticle
import com.example.sleep.newsapi.NewsApiEndpoint
import com.example.sleep.newsapi.NewsApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ArticleRepository(application: Application) {
    private val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val db = Room.databaseBuilder(
        application.applicationContext,
        ArticleDatabase::class.java,
        "articles.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.articleDao()

    fun getArticles() : Flow<List<Article>> {
        return dao.getAll()
    }

    suspend fun addNewArticles(keyword: String) {
        val articles = getNewsApiArticles(keyword)
        articles?.let { addToDatabase(articles) }
    }

    private suspend fun getNewsApiArticles(keyword: String): List<NewsApiArticle>? {
        // Fetch the articles in a loop since a limited number of articles can be retrieved
        // in a single request

        val pageSize = 100
        var page = 1

        val allArticles = mutableListOf<NewsApiArticle>()

        var done = false
        while (!done) {
            // Suspend the coroutine until Retrofit returns with the results
            val newsApiResponse = suspendCoroutine<NewsApiResponse?> { continuation ->
                retrofit.create(NewsApiEndpoint::class.java).fetch(keyword, page, pageSize)
                    .enqueue(object : Callback<NewsApiResponse> {
                        override fun onResponse(call: Call<NewsApiResponse>, response: Response<NewsApiResponse>) {
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
        val dbArticles = articles.map { Article(it.url, it.publishedAt.time, it.title, it.urlToImage, it.content) }
        dao.insert(dbArticles)
    }
}