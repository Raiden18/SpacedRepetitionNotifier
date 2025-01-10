package org.danceofvalkyries.notion.api.rest.response

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.notion.api.rest.response.properties.PropertyResponse

data class NotionPageResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("id") val id: String,
    @SerializedName("created_time") val createdTime: String,
    @SerializedName("last_edited_time") val lastEditedTime: String,
    @SerializedName("created_by") val createdBy: UserResponse,
    @SerializedName("last_edited_by") val lastEditedBy: UserResponse,
    @SerializedName("cover") val cover: Any?,
    @SerializedName("icon") val icon: Any?,
    @SerializedName("parent") val parent: ParentResponse,
    @SerializedName("archived") val archived: Boolean,
    @SerializedName("in_trash") val inTrash: Boolean,
    @SerializedName("properties") val properties: Map<String, PropertyResponse>,
    @SerializedName("url") val url: String,
    @SerializedName("public_url") val publicUrl: Any?
)