package com.example.news_app

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.news_app.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable JavaScript for better web page rendering
        binding.webView.settings.javaScriptEnabled = true

        // Extract the news URL from the intent
        val newsUrl = intent.getStringExtra("news_url")

        // Load the news URL in the WebView
        if (newsUrl != null) {
            binding.webView.loadUrl(newsUrl)
        }

        // Enable WebChromeClient for progress tracking
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Handle back navigation for WebView
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}