package org.danceofvalkyries.telegram.jsonobjects

import com.google.gson.annotations.SerializedName

data class TelegramMessageRootResponse(
    @SerializedName("error_code")
    val errorCode: Int? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("result")
    val result: MessageData? = null,
)
