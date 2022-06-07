package com.example.sleep.newsapi

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsApiArticle>
)
