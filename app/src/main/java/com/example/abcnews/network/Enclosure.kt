package com.example.abcnews.network

import com.fasterxml.jackson.annotation.JsonProperty

data class Enclosure(
    @JsonProperty("link") val link: String = "",
    @JsonProperty("type") val type: String = "",
    @JsonProperty("thumbnail") val thumbnail: String = ""
)