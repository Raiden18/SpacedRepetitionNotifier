package org.danceofvalkyries.notion.data.repositories.api.response

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("database_id")
    val databaseId: String?
)