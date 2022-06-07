package com.example.sleep.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Article(
    @PrimaryKey val url: String,
    val publishedAt: Long,
    val title: String,
    val urlToImage: String?,
    val content: String
) : Serializable
