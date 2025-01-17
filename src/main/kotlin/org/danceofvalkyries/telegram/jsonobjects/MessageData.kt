package org.danceofvalkyries.telegram.jsonobjects

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.job.data.telegram.jsons.ReplyMarkupData

/**
 * Telegram Message request has the same body for photo and text.
 *
 * For TextMessage we can have only text.
 * For PhotoMessage we can have only caption.
 *
 * @property caption
 * @property text
 */
data class MessageData(
    @SerializedName("chat_id")
    val chatId: String? = null,
    @SerializedName("parse_mode")
    val parseMode: String? = null,
    @SerializedName("reply_markup")
    val replyMarkup: ReplyMarkupData? = null,
    @SerializedName("photo")
    val photo: Any? = null,
    @SerializedName("caption")
    val caption: String? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("message_id")
    val messageId: Long? = null,
)