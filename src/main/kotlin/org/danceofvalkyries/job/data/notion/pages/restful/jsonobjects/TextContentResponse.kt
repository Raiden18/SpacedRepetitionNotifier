package org.danceofvalkyries.job.data.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class TextContentResponse(
    @SerializedName("content") val content: String?,
    @SerializedName("link") val link: Any?
)