package org.danceofvalkyries.app.data.telegram.jsons

import com.google.gson.annotations.SerializedName

data class TelegramMessageRootResponse(
    @SerializedName("result")
    val result: MessageData?
)
