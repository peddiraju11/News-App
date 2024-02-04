package com.example.news_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NewsViewModel : ViewModel() {

    private val _newsArticle = MutableLiveData<List<Article>>()
    val newsArticle: LiveData<List<Article>> get() = _newsArticle
    private var isAscendingOrder = true

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchNews(apiUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val newsList = getNewsData(apiUrl)
            _newsArticle.postValue(newsList)
        }
    }

    private fun getNewsData(apiUrl: String): List<Article> {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        val newsList = mutableListOf<Article>()

        try {
            val inputStreamReader = InputStreamReader(connection.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val response = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }

            val newsModel = convertJsonToObject<NewsModel>(response.toString())
            Log.d("Peddy", "news size - ${newsModel?.articles?.size}")
            newsList.addAll(newsModel?.articles ?: emptyList())

        } finally {
            connection.disconnect()
        }

        return newsList
    }

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

        _newsArticle.postValue(sortedNews)
        isAscendingOrder = !isAscendingOrder // Toggle the sorting order
    }

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