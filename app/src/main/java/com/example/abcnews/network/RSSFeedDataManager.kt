package com.example.abcnews.network

import android.os.Handler
import android.os.Looper
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

object RSSFeedDataManager {
    private const val DELAY = 3000L // 3 sec delay
    private var url = URL("https://api.rss2json.com/v1/api.json?rss_url=http://www.abc.net.au/news/feed/51120/rss.xml")
    private var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    private val mapper = ObjectMapper()
    private lateinit var listener: RSSFeedDataListener
    private val itemList: MutableList<FeedItemViewModel> = mutableListOf()
    private var handler: Handler = Handler(Looper.getMainLooper())

    fun startPolling() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchData()
                handler.postDelayed(this, DELAY)
            }
        }, DELAY)
        fetchData()
    }

    // this method is removing handler before adding again in case of network connecting twice / fetch delays
    fun stopPolling() {
        handler.removeCallbacksAndMessages(null)
    }

    fun addListener(listener: RSSFeedDataListener) {
        this.listener = listener
    }

    private fun fetchData() {
        // fetching data in coroutine to avoid blocking UI thread. this is an async task which is running in background.
        GlobalScope.launch(Dispatchers.IO) {
            try {
                itemList.clear()
                val feedItemModel = mapper.readValue(url, FeedItemModel::class.java)
                itemList.add(feedItemModel.feed)
                itemList.addAll(feedItemModel.items)
                // Here used Dispatchers.Main to post on MainActivity since Navigation starts from here.
                launch(Dispatchers.Main) {
                    listener.dataUpdated(itemList)
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    listener.dataUpdateFailed()
                }
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    interface RSSFeedDataListener {
        fun dataUpdated(newItemList: List<FeedItemViewModel>)
        fun dataUpdateFailed()
    }
}