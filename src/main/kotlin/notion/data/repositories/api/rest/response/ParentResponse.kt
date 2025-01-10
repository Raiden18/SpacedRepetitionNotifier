package org.danceofvalkyries.notion.data.repositories.api.rest.response

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String,
    @SerializedName("database_id") val databaseId: String
)