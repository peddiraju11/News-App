package com.example.news_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.news_app.model.NewsArticle
import com.example.news_app.model.NewsModel
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// ViewModel responsible for managing news data and business logic
class NewsViewModel : ViewModel() {

    // LiveData for holding a list of news articles
    private val _newsArticle = MutableLiveData<List<NewsArticle>>()
    val newsArticle: LiveData<List<NewsArticle>> get() = _newsArticle

    // Flag indicating the sorting order of news articles
    var isAscendingOrder = true

    // Coroutine function to fetch news data from the provided API URL
    @OptIn(DelicateCoroutinesApi::class)
    fun fetchNews(apiUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val newsList = getNewsData(apiUrl)
            _newsArticle.postValue(newsList)
        }
    }

    // Function to retrieve news data from the specified API URL
    private fun getNewsData(apiUrl: String): List<NewsArticle> {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        val newsList = mutableListOf<NewsArticle>()

        try {
            val inputStreamReader = InputStreamReader(connection.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val response = StringBuilder()
            var line: String?

            // Read the API response and append it to the StringBuilder
            while (bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Convert JSON response to NewsModel object using Gson
            val newsModel = convertJsonToObject<NewsModel>(response.toString())
            Log.d("Peddy", "news size - ${newsModel?.articles?.size}")

            // Add the articles to the newsList
            newsList.addAll(newsModel?.articles ?: emptyList())

        } finally {
            // Disconnect the HttpURLConnection
            connection.disconnect()
        }

        return newsList
    }

    // Function to sort news articles based on date, with ascending or descending order
    fun sortNewsByDate() {
        val currentNews = _newsArticle.value.orEmpty()
        val sortedNews = if (isAscendingOrder) {
            currentNews.sortedBy {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = dateFormat.parse(it.publishedAt)
                date
            }
        } else {
            currentNews.sortedByDescending {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = dateFormat.parse(it.publishedAt)
                date
            }
        }

        // Post the sorted news list to LiveData
        _newsArticle.postValue(sortedNews)
        // Toggle the sorting order for the next click
        isAscendingOrder = !isAscendingOrder
    }

    // Generic function to convert JSON string to a specified class using Gson
    private inline fun <reified T> convertJsonToObject(jsonString: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(jsonString, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}