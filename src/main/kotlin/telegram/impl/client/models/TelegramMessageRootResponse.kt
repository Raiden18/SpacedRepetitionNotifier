package org.danceofvalkyries.telegram.impl.client.models

import com.google.gson.annotations.SerializedName

data class TelegramMessageRootResponse(
    @SerializedName("result")
    val result: MessageData
)
