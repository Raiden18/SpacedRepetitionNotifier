package org.danceofvalkyries.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("database_id")
    val databaseId: String?
)