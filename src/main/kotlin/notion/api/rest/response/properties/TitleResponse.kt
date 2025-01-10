package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class TitleResponse(
    @SerializedName("title") val title: List<TextResponse>
)