package org.danceofvalkyries.notion.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class CheckboxResponse(
    @SerializedName("checkbox") val checkbox: Boolean
)