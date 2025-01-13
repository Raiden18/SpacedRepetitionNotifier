package org.danceofvalkyries.notion.impl.restapi.models.response

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("database_id")
    val databaseId: String?
)