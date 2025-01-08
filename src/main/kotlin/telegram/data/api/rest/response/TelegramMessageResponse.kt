package org.danceofvalkyries.telegram.data.api.rest.response

import com.google.gson.annotations.SerializedName

data class TelegramMessageRootResponse(
    @SerializedName("result")
    val result: TelegramMessageResponse
)

data class TelegramMessageResponse(
    @SerializedName("message_id")
    val messageId: Long,
    @SerializedName("date")
    val date: Long,
    @SerializedName("text")
    val text: String,
)