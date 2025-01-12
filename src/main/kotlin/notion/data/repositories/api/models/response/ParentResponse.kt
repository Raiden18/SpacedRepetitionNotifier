package org.danceofvalkyries.notion.data.repositories.api.models.response

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("database_id")
    val databaseId: String?
)