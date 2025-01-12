package org.danceofvalkyries.notion.data.repositories.api.models

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.notion.data.repositories.api.models.response.properties.RichTextResponse
import org.danceofvalkyries.notion.data.repositories.api.models.response.properties.TextResponse

data class PropertyData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("checkbox") val checkbox: Boolean? = null,
    @SerializedName("rich_text") val richText: List<RichTextResponse>? = null,
    @SerializedName("title") val title: List<TextResponse>? = null,
)