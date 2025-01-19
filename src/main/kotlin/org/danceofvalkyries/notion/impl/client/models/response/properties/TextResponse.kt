package org.danceofvalkyries.notion.impl.client.models.response.properties

import com.google.gson.annotations.SerializedName

data class TextResponse(
    @SerializedName("text") val text: TextContentResponse?,
    @SerializedName("plain_text") val plainText: String?,
)