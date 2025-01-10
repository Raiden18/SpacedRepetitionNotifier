package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class RichTextResponse(
    @SerializedName("rich_text") val richText: List<TextResponse>
)