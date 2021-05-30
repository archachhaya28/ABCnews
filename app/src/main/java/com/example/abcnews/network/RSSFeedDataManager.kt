package com.example.abcnews.network

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.HttpURLConnection
import java.net.URL

object RSSFeedDataManager {
    private var url = URL("https://api.rss2json.com/v1/api.json?rss_url=http://www.abc.net.au/news/feed/51120/rss.xml")
    private var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    private val mapper = ObjectMapper()
    private lateinit var listener: RSSFeedDataListener

    fun startPolling() {
        getData()
    }

    private fun getData() {
        try {
            val itemList: MutableList<FeedItemViewModel> = mutableListOf()
            val feedItemModel = mapper.readValue(url, FeedItemModel::class.java)
            itemList.add(feedItemModel.feed)
            itemList.addAll(feedItemModel.items)
            //itemList.addAll(listOf(feedItemModel))
            //itemList.forEach { it -> it. { Log.d("XXX: ", it.title) } }
            listener.dataUpdated(itemList)

        } catch (e: Exception) {
            print(e.printStackTrace())
            listener.dataUpdateFailed()
        } finally {
            urlConnection.disconnect()
        }
    }

    fun stopPolling() {
        // remove handler if there is a situation to pull every X seconds.
    }

    fun addListener(listener: RSSFeedDataListener) {
        this.listener = listener
    }

    interface RSSFeedDataListener {
        fun dataUpdated(newItemList: MutableList<FeedItemViewModel>)
        fun dataUpdateFailed()
    }
}