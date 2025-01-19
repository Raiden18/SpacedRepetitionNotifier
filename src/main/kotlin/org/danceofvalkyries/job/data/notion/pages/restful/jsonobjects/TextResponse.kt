package org.danceofvalkyries.job.data.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class TextResponse(
    @SerializedName("text") val text: TextContentResponse?,
    @SerializedName("plain_text") val plainText: String?,
)