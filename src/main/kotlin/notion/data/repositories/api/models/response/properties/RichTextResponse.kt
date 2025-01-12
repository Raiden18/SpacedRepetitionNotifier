package org.danceofvalkyries.notion.data.repositories.api.models.response.properties

import com.google.gson.annotations.SerializedName

data class RichTextResponse(
    @SerializedName("text") val text: TextContentResponse
)