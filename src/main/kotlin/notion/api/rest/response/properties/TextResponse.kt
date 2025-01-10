package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class TextResponse(
    @SerializedName("text") val text: TextContentResponse,
    @SerializedName("plain_text") val plainText: String,
    @SerializedName("href") val href: Any?
)