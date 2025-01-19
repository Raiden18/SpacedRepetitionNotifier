package org.danceofvalkyries.notion.pages.restful.jsonobjects

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("id") val id: String
)