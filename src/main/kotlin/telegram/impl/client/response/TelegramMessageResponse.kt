package org.danceofvalkyries.telegram.impl.client.response

import com.google.gson.annotations.SerializedName

data class TelegramMessageResponse(
    @SerializedName("message_id")
    val messageId: Long,
    @SerializedName("date")
    val date: Long,
    @SerializedName("text")
    val text: String,
)