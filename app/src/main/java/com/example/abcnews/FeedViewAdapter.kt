package com.example.abcnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abcnews.network.*
import com.google.android.material.imageview.ShapeableImageView

class FeedViewAdapter(private val listener: ViewCallBack?): RecyclerView.Adapter<FeedViewAdapter.FeedViewHolder>() {

    var itemList: MutableList<FeedItemViewModel> = mutableListOf()

    interface ViewCallBack {
        fun onItemClicked(feedItemViewModel: FeedItemViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val layoutId = when (viewType) {
            TYPE_ITEM -> R.layout.view_list_item
            TYPE_FEED -> R.layout.view_list_feed
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return FeedViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.onBindView(itemList[position], listener)
    }

    override fun getItemViewType(position: Int): Int {
        return itemList[position].type
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBindView(viewModel: FeedItemViewModel, listener: ViewCallBack?) {
            when(viewModel) {
                is Items -> {
                    itemView.findViewById<TextView>(R.id.titleTextView).text = viewModel.title
                    Glide.with(itemView.context)
                        .load(viewModel.thumbnail)
                        .into(itemView.findViewById<ShapeableImageView>(R.id.imageView))

                    itemView.findViewById<TextView>(R.id.dateTextView).text = viewModel.getDayAndTime(viewModel.publishedDate.time)
                }
                is Feed -> {
                    itemView.findViewById<TextView>(R.id.feedTitleTextView).text = viewModel.title
                    Glide.with(itemView.context)
                        .load(viewModel.image)
                        .into(itemView.findViewById<ShapeableImageView>(R.id.feedImageView))
                }
            }
            itemView.setOnClickListener { listener?.onItemClicked(viewModel) }
        }
    }
}