package com.example.sleep

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sleep.db.Article

class ArticleActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MESSAGE = "com.example.sleep.EXTRA_MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article)

        val article = intent.getSerializableExtra(EXTRA_MESSAGE) as Article

        val imageView = findViewById<ImageView>(R.id.image)
        Glide.with(this).load(article.urlToImage).centerCrop().placeholder(R.drawable.placeholder).into(imageView)

        val title = findViewById<TextView>(R.id.title)
        title.text = article.title

        val content = findViewById<TextView>(R.id.content)
        content.text = article.content
    }
}
