package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class PropertyResponse(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("checkbox") val checkbox: CheckboxResponse?,
    @SerializedName("number") val number: NumberData?,
    @SerializedName("rich_text") val richText: RichTextResponse?,
    @SerializedName("created_time") val createdTime: CreatedTime?,
    @SerializedName("title") val title: TitleResponse?
)