package com.example.abcnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.abcnews.network.RSSFeedDataManager
import com.example.abcnews.service.NetworkConnectivityService
import com.example.abcnews.service.ServiceFinder

class MainActivity: AppCompatActivity() {

    private lateinit var networkConnectivityListener:
            NetworkConnectivityService.ConnectivityStateListener
    private val noNetworkTextView: TextView by lazy { findViewById(R.id.noNetworkTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        networkConnectivityListener = object : NetworkConnectivityService.ConnectivityStateListener {
            override fun onStateChange(state: NetworkConnectivityService.NetworkState) {
                if (state.isConnected()) {
                    noNetworkTextView.visibility = View.GONE
                    RSSFeedDataManager.stopPolling()
                    RSSFeedDataManager.startPolling()
                } else {
                    noNetworkTextView.visibility = View.VISIBLE
                }
            }
        }
        ServiceFinder.networkService.addListener(networkConnectivityListener)
    }

    override fun onPause() {
        super.onPause()
        RSSFeedDataManager.stopPolling()
        ServiceFinder.networkService.removeListener(networkConnectivityListener)
    }
}