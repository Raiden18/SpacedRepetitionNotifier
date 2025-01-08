package org.danceofvalkyries.telegram.data.api.rest.request.bodies

import com.google.gson.Gson
import org.danceofvalkyries.json.`object`

fun SendMessageBody(
    gson: Gson,
    chatId: String,
    text: String
): String {
    return `object` {
        "chat_id" to chatId
        "text" to text
        "parse_mode" to "Markdown"
    }.let(gson::toJson)
}

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
