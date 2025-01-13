package org.danceofvalkyries.telegram.impl.client.response

import com.google.gson.annotations.SerializedName

data class TelegramMessageRootResponse(
    @SerializedName("result")
    val result: TelegramMessageResponse
)
