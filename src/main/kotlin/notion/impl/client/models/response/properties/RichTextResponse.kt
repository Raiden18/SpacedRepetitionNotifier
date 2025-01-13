package org.danceofvalkyries.notion.impl.restapi.models.response.properties

import com.google.gson.annotations.SerializedName

data class RichTextResponse(
    @SerializedName("text") val text: TextContentResponse
)