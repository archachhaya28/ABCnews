package com.example.abcnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.abcnews.ListDetailFragment.Companion.ITEM_ID
import com.example.abcnews.network.Feed
import com.example.abcnews.network.FeedItemViewModel
import com.example.abcnews.network.RSSFeedDataManager
import com.example.abcnews.service.NetworkConnectivityService
import com.example.abcnews.service.ServiceFinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity: AppCompatActivity(),
        RSSFeedDataManager.RSSFeedDataListener,
        FeedViewAdapter.ViewCallBack {

    private lateinit var networkConnectivityListener:
            NetworkConnectivityService.ConnectivityStateListener

    private val noNetworkTextView: TextView by lazy { findViewById(R.id.noNetworkTextView) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var detailFragment: ListDetailFragment

    private var adapter: FeedViewAdapter = FeedViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RSSFeedDataManager.addListener(this)

        recyclerView = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.cyan600, null))
        swipeRefreshLayout.setOnRefreshListener {
            fetchFeedItems()
        }
        recyclerView.adapter = adapter
    }

    private fun fetchFeedItems() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                RSSFeedDataManager.startPolling()
                withContext(Dispatchers.Main) {
                    updateViews()
                }
            }
        }
    }

    private fun updateViews() {
        swipeRefreshLayout.isRefreshing = false
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        networkConnectivityListener = object : NetworkConnectivityService.ConnectivityStateListener {
            override fun onStateChange(state: NetworkConnectivityService.NetworkState) {
                if (state.isConnected()) {
                    noNetworkTextView.visibility = View.GONE
                    RSSFeedDataManager.stopPolling()
                    fetchFeedItems()

                } else {
                    noNetworkTextView.visibility = View.VISIBLE
                }
            }

        }
        ServiceFinder.networkService.addListener(networkConnectivityListener)
    }

    override fun dataUpdated(newItemList: MutableList<FeedItemViewModel>) {
        adapter.itemList.clear()
        adapter.itemList = newItemList
    }

    override fun dataUpdateFailed() {
       // show error message. not covered here.
    }

    override fun onPause() {
        super.onPause()
        ServiceFinder.networkService.removeListener(networkConnectivityListener)
    }

    override fun onItemClicked(feedItemViewModel: FeedItemViewModel) {
        if (feedItemViewModel is Feed) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(feedItemViewModel.link))
            startActivity(browserIntent)
        } else {
            detailFragment = ListDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM_ID, feedItemViewModel)
                }
            }
            supportFragmentManager.beginTransaction().add(R.id.frameLayout, detailFragment).commit()
        }
    }

    override fun onBackPressed() {
        if (detailFragment.isAdded)
            supportFragmentManager.beginTransaction().remove(detailFragment).commit()
        else
            super.onBackPressed()
    }
}