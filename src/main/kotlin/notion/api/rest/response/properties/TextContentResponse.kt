package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class TextContentResponse(
    @SerializedName("content") val content: String,
    @SerializedName("link") val link: Any?
)