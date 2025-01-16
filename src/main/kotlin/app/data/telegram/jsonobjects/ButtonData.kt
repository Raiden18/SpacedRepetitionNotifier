package org.danceofvalkyries.app.data.telegram.jsons

import com.google.gson.annotations.SerializedName

data class ButtonData(
    @SerializedName("text")
    val text: String,
    @SerializedName("callback_data")
    val callbackData: String? = null,
    @SerializedName("url")
    val url: String? = null,
)