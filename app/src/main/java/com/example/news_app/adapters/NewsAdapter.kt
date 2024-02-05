package com.example.news_app.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.news_app.R
import com.example.news_app.model.NewsArticle
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

// Adapter for displaying news articles in a RecyclerView
class NewsAdapter(private val context: Context, private val articleList: List<NewsArticle>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    // Create a new ViewHolder when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the current news article
        val news = articleList[position]

        // Set the title and description in the ViewHolder
        holder.title.text = news.title
        holder.description.text = news.description

        // Parse and format the published date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = dateFormat.parse(news.publishedAt)
        val formattedDate = date?.let {
            SimpleDateFormat("d MMM, yyyy HH:mm", Locale.getDefault()).format(it)
        }
        holder.publishDate.text = formattedDate.orEmpty()

        // Load the image using Picasso library
        Picasso.get().load(news.urlToImage).into(holder.image)

        // Handle item click to open the news article in a browser
        holder.itemView.setOnClickListener {
            with(news.url) {
                if (!this.startsWith("http://") && !this.startsWith("https://")) {
                    news.url = "http://$this"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this))
                context.startActivity(browserIntent)
            }
        }
    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return articleList.size
    }

    // ViewHolder class to hold references to the views within each item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val publishDate: TextView = itemView.findViewById(R.id.publishDate)
    }
}
