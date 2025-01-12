package org.danceofvalkyries.notion.data.repositories.api.models.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("id") val id: String
)