package com.example.abcnews.network

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class FeedItemModel(
    @JsonProperty("status") val status: String = "",
    @JsonProperty("feed") val feed: Feed = Feed(),
    @JsonProperty("items") val items: List<Items> = listOf()
): Serializable