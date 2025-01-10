package org.danceofvalkyries.notion.api.rest.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("id") val id: String
)