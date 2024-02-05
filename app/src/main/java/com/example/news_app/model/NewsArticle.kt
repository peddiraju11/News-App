package com.example.news_app.model

// Data class representing a news article
data class NewsArticle(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    var url: String,
    val urlToImage: String
)