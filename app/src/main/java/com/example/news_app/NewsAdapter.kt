package com.example.news_app
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(private val context: Context, private val articleList: List<Article>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = articleList[position]
        holder.title.text = news.title
        holder.description.text = news.description
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = dateFormat.parse(news.publishedAt)
        val formattedDate = date?.let {
            SimpleDateFormat("d MMM, yyyy HH:mm", Locale.getDefault()).format(
                it
            )
        }
        holder.publishDate.text = formattedDate.orEmpty()

        Picasso.get().load(news.urlToImage).into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("news_url", news.url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val publishDate: TextView = itemView.findViewById(R.id.publishDate)
    }
}