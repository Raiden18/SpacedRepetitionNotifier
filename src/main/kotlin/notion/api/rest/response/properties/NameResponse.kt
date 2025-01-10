package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class NameResponse(
    @SerializedName("title") val title: List<TextResponse>
)