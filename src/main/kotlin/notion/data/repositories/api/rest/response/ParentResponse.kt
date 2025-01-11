package org.danceofvalkyries.notion.data.repositories.api.rest.response

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("database_id")
    private val databaseId_: String?
) {
    val databaseId: String?
        get() = databaseId_?.replace("-", "")
}