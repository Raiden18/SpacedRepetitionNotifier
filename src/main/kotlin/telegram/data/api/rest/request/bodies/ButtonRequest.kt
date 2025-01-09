package org.danceofvalkyries.telegram.data.api.rest.request.bodies

import com.google.gson.annotations.SerializedName

data class ButtonRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("callback_data")
    val callbackData: String,
    @SerializedName("url")
    val url: String?
)