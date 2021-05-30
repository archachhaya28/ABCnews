package com.example.abcnews.network

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val TYPE_ITEM: Int = 0
const val TYPE_FEED: Int = 1

sealed class FeedItemViewModel(val type: Int = TYPE_ITEM): Serializable

data class Items(
    @JsonProperty("title") val title: String = "",
    @JsonProperty("pubDate") val pubDate: String = "",
    @JsonProperty("link") val link: String = "",
    @JsonProperty("guid") val guid: String = "",
    @JsonInclude(JsonInclude.Include.NON_EMPTY)@JsonProperty("author") val author: String = "",
    @JsonProperty("thumbnail") val thumbnail: String = "",
    @JsonProperty("description") val description: String = "",
    @JsonProperty("content") val content: String = "",
    @JsonProperty("enclosure") val enclosure: Enclosure = Enclosure(),
    @JsonProperty("categories") val categories: List<String> = listOf())
    :FeedItemViewModel(TYPE_ITEM) {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH)

    val publishedDate: Date by lazy {
        parseDateString(pubDate)
    }

    private fun parseDateString(dateString: String): Date {
        return if (dateString.isNotEmpty()) {
            try {
                dateFormat.parse(dateString)
            } catch (e: ParseException) {
                null
            } ?: Date(0)
        } else {
            Date(0)
        }
    }

    fun getDayAndTime(time: Long): String {
        return dateFormat.format(time)
    }
}

data class Feed(
    @JsonProperty("url") val url: String = "",
    @JsonProperty("title") val title: String = "",
    @JsonProperty("link") val link: String = "",
    @JsonProperty("author") val author: String = "",
    @JsonProperty("description") val description: String = "",
    @JsonProperty("image") val image: String = ""
): FeedItemViewModel(TYPE_FEED)