package org.danceofvalkyries.notion.data.repositories.api.response.properties

import com.google.gson.annotations.SerializedName

data class CheckboxResponse(
    @SerializedName("checkbox") val checkbox: Boolean
)