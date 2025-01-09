package org.danceofvalkyries.telegram.data.api.rest.request.bodies

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    @SerializedName("chat_id")
    val chatId: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("parse_mode")
    val parseMode: String,
    @SerializedName("reply_markup")
    val replyMarkup: ReplyMarkupResponse?,
)