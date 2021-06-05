package com.example.abcnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.abcnews.network.Feed
import com.example.abcnews.network.FeedItemViewModel
import com.example.abcnews.network.RSSFeedDataManager
import java.lang.IllegalArgumentException

class ListFragment: Fragment(),
    FeedViewAdapter.ViewCallBack,
    RSSFeedDataManager.RSSFeedDataListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var adapter: FeedViewAdapter = FeedViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RSSFeedDataManager.addListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.cyan600, null))
        swipeRefreshLayout.setOnRefreshListener {
            RSSFeedDataManager.stopPolling()
            RSSFeedDataManager.startPolling()
        }
        recyclerView = view.findViewById(R.id.listView)
        recyclerView.adapter = adapter

        return view
    }

    override fun dataUpdated(newItemList: List<FeedItemViewModel>) {
        adapter.itemList.clear()
        adapter.itemList.addAll(newItemList)
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    // this method is to show error message when data failed loading from server
    override fun dataUpdateFailed() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClicked(feedItemViewModel: FeedItemViewModel) {
        if (feedItemViewModel is Feed) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(feedItemViewModel.link))
            startActivity(browserIntent)
        } else {
            // added try block to ensure safe navigation.
            try {
                val action = ListFragmentDirections.navigateToListDetail(feedItemViewModel)
                findNavController().navigate(action)
            } catch (e: IllegalArgumentException) {

            } catch (e: IllegalStateException) {

            }
        }
    }
}