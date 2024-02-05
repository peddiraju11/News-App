package com.example.news_app.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news_app.R
import com.example.news_app.adapters.NewsAdapter
import com.example.news_app.databinding.ActivityMainBinding
import com.example.news_app.viewmodel.NewsViewModel

class MainActivity : AppCompatActivity() {

    // View binding for the activity
    private lateinit var binding: ActivityMainBinding

    // ViewModel for managing data and business logic
    private val viewModel: NewsViewModel by viewModels()

    // Launcher for the permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, show a toast
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission not granted, show a toast with a message
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInternetConnection()
    }

    // Function to ask for notification permission
    private fun askNotificationPermission() {
        // Request permission only for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Function to set up notification channels
    private fun setNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.app_name)
            val channelName = getString(R.string.app_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }
    }

    //Check whether Internet connection is available or not
    private fun checkInternetConnection() {
        if (isNetworkAvailable()) {
            // Internet connection is available
            with(binding) {
                noInterNetText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
            }

            // Request notification permission and set up notification channels
            askNotificationPermission()
            setNotificationChannels()

            // Configure RecyclerView and show loading progress
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.progressBar.visibility = View.VISIBLE

            // Observe changes in the news list
            viewModel.newsArticle.observe(this) { newsList ->
                // Hide progress bar and show RecyclerView when data is available
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                // Log the size of the news list for debugging purposes
                Log.d("Peddy", "news size - ${newsList.size}")

                // Display the fetched news in the RecyclerView
                newsList?.let {
                    binding.recyclerView.adapter = NewsAdapter(this, it)
                }
            }

            // Set up click listener for the sort button
            binding.sortButton.setOnClickListener {
                // Sort the news based on date (assuming NewsModel has a date property)
                viewModel.sortNewsByDate()

                // Toggle the button text based on sorting order
                if (viewModel.isAscendingOrder)
                    binding.sortButton.text = this.getString(R.string.new_to_old)
                else
                    binding.sortButton.text = this.getString(R.string.old_to_new)
            }

            // Execute ViewModel to fetch news data
            viewModel.fetchNews("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")

        } else {
            // No internet connection
            showMessage()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo?.isConnected == true
        }
    }

    private fun showMessage() {
        // Display the message in a TextView or any other UI component
        with(binding) {
            noInterNetText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }
}
