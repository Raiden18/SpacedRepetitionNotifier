package org.danceofvalkyries.notion.data.repositories.api.response.properties

import com.google.gson.annotations.SerializedName

data class TextContentResponse(
    @SerializedName("content") val content: String?,
    @SerializedName("link") val link: Any?
)