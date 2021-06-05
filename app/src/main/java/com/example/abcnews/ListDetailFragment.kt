package com.example.abcnews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.abcnews.network.FeedItemViewModel
import com.example.abcnews.network.Items
import com.google.android.material.imageview.ShapeableImageView

class ListDetailFragment: Fragment() {

    private lateinit var feedItemViewModel: FeedItemViewModel
    private lateinit var authorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                feedItemViewModel = it.getSerializable(ITEM_ID) as FeedItemViewModel
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list_details, container, false)
        rootView.findViewById<TextView>(R.id.detailTitleTextView).text = (feedItemViewModel as Items).title
        rootView.findViewById<TextView>(R.id.detailContentTextView).text = (feedItemViewModel as Items).content
        rootView.findViewById<TextView>(R.id.detailDateTextView).text =
                (feedItemViewModel as Items).getDayAndTime((feedItemViewModel as Items).publishedDate.time)
        authorTextView = rootView.findViewById(R.id.detailAuthorTextView)
        if ((feedItemViewModel as Items).author.isEmpty())
            authorTextView.visibility = View.GONE
         else
            authorTextView.visibility = View.VISIBLE
        authorTextView.text = (feedItemViewModel as Items).author
        Glide.with(rootView.context)
            .load((feedItemViewModel as Items).thumbnail)
            .into(rootView.findViewById<ShapeableImageView>(R.id.detailImageView))

        return rootView
    }

    companion object {
        const val ITEM_ID = "item"
    }
}