package org.danceofvalkyries.notion.data.repositories.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class CreatedTime(
    @SerializedName("created_time") val createdTime: String
)