package org.danceofvalkyries.telegram.data.api.rest.request.bodies

import com.google.gson.annotations.SerializedName

/**
 * Telegram Message request has the same body for photo and text.
 *
 * For TextMessage we can have only text.
 * For PhotoMessage we can have only caption.
 *
 * @property caption
 * @property text
 */
data class SendMessageRequest(
    @SerializedName("chat_id")
    val chatId: String,
    @SerializedName("parse_mode")
    val parseMode: String,
    @SerializedName("reply_markup")
    val replyMarkup: ReplyMarkupResponse?,
    @SerializedName("photo")
    val photo: String?,

    /**
     * Telegram has the sme
     */
    @SerializedName("caption")
    val caption: String?,
    @SerializedName("text")
    val text: String?,
)