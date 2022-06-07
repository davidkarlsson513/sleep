package com.example.sleep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sleep.db.Article

class ArticleListAdapter(private val callback: Callback) : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    interface Callback {
        fun onOpenArticle(article: Article)
    }

    private var articles: List<Article> = emptyList()

    fun setArticles(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.article_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val article = articles[position]
        with(viewHolder) {
            view.setOnClickListener { callback.onOpenArticle(article) }
            title.text = article.title
            content.text = article.content
            Glide.with(itemView).load(article.urlToImage).centerCrop().placeholder(R.drawable.placeholder).into(image)
        }
    }

    override fun getItemCount() = articles.size
}
