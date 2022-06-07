package com.example.sleep

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sleep.db.Article
import kotlin.math.max

class MainActivity : AppCompatActivity(), CustomAdapter.Callback {

    private lateinit var model: MainViewModel

    private var gridLayoutManager = GridLayoutManager(this, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(this)[MainViewModel::class.java]

        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Add listener to configure the layout manager with the number of columns when the size
        // of the grid changes
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            val itemSize = resources.getDimension(R.dimen.article_item_size).toInt()
            val width = recyclerView.measuredWidth
            val count = max(width / itemSize, 1)

            // Prevent an infinite loop by only updating the layout manager if needed
            if (gridLayoutManager.spanCount != count) {
                gridLayoutManager = GridLayoutManager(this, count)
                recyclerView.layoutManager = gridLayoutManager
            }
        }

        recyclerView.layoutManager = gridLayoutManager
        val customAdapter = CustomAdapter(this)
        recyclerView.adapter = customAdapter

        model.getArticles().observe(this) {
            customAdapter.setArticles(it)
        }
    }

    override fun onResume() {
        super.onResume()

        // Fetch new articles
        val terms = listOf("Apple", "Google", "Meta")
        terms.forEach { model.addNewArticles(it) }
    }

    override fun onOpenArticle(article: Article) {
        // Launch article activity
        val intent = Intent(this, ArticleActivity::class.java).apply {
            putExtra(ArticleActivity.EXTRA_MESSAGE, article)
        }
        startActivity(intent)
    }
}
