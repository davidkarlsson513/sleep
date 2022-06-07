package com.example.sleep.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(articles: List<Article>)

    @Query("SELECT * FROM article ORDER BY publishedAt")
    fun getAll(): Flow<List<Article>>
}
