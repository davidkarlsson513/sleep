package com.example.sleep

import android.app.Application
import androidx.room.Room
import com.example.sleep.db.ArticleDao
import com.example.sleep.db.ArticleDatabase

// TOOD: Rename?
class App : Application() {
    lateinit var articleDao: ArticleDao

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            applicationContext,
            ArticleDatabase::class.java,
            "articles.db"
        ).fallbackToDestructiveMigration().build()

        articleDao = db.articleDao()
    }
}
