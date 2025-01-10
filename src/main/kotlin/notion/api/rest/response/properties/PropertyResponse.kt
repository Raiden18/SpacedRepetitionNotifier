package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class PropertyResponse(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("checkbox") val checkbox: Boolean?,
    @SerializedName("rich_text") val richText: List<RichTextResponse>?,
    @SerializedName("Name") val name: NameResponse?
)