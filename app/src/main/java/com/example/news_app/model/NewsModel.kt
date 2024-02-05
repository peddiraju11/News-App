package com.example.news_app.model

// Data class representing a model for a collection of news articles
data class NewsModel(
    val articles: List<NewsArticle>,
    val status: String
)