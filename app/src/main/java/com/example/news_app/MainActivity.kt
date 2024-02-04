package com.example.news_app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access views using binding
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.progressBar.visibility = View.VISIBLE

        // Observe changes in the news list
        viewModel.newsArticle.observe(this) { newsList ->
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            Log.d("peddy", "news size - ${newsList.size}")
            newsList?.let {
                // Display the fetched news in the RecyclerView
                binding.recyclerView.adapter = NewsAdapter(this, it)
            }
        }

        // Set up click listener for the sort button
        binding.sortButton.setOnClickListener {
            // Sort the news based on date (assuming NewsModel has a date property)
            viewModel.sortNewsByDate()
        }

        // Execute ViewModel to fetch news data
        viewModel.fetchNews("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")
    }

}