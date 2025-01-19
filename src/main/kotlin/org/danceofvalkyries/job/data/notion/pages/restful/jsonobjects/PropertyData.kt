package org.danceofvalkyries.job.data.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class PropertyData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("checkbox") val checkbox: Boolean? = null,
    @SerializedName("rich_text") val richText: List<RichTextResponse>? = null,
    @SerializedName("title") val title: List<TextResponse>? = null,
)