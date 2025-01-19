package org.danceofvalkyries.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class RichTextResponse(
    @SerializedName("text") val text: TextContentResponse
)