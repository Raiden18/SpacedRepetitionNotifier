package org.danceofvalkyries.telegram.impl.models

import com.google.gson.annotations.SerializedName

data class ButtonData(
    @SerializedName("text")
    val text: String,
    @SerializedName("callback_data")
    val callbackData: String?,
    @SerializedName("url")
    val url: String?
)