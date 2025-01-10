package org.danceofvalkyries.notion.data.repositories.api.rest.response.properties

import com.google.gson.annotations.SerializedName

data class NumberData(
    @SerializedName("number") val number: Double?
)