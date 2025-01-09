package org.danceofvalkyries.telegram.data.api.rest.request.bodies

import com.google.gson.Gson
import org.danceofvalkyries.utils.rest.`object`


fun EditMessageBody(
    gson: Gson,
    chatId: String,
    text: String,
    messageId: String,
): String {
    return `object` {
        "chat_id" to chatId
        "text" to text
        "message_id" to messageId
    }.let(gson::toJson)
}
