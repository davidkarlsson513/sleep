package com.example.sleep

import java.util.Date

data class NewsApiArticle(
    val source: NewsApiSource,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Date,
    val content: String
)
