package org.danceofvalkyries.telegram.data.api.mappers

import org.danceofvalkyries.telegram.data.api.rest.request.bodies.ButtonRequest
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.ReplyMarkupResponse
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.SendMessageRequest
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

fun TelegramMessageBody.textWithEscapedCharacters(): String {
    return text.replace("!", "\\!")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("=", "\\=")
        .replace(".", "\\.")
}

// TODO: Add Unit tests when I've figured out what to do with callbackData
fun TelegramMessageBody.toRequest(chatId: String): SendMessageRequest {
    return SendMessageRequest(
        chatId = chatId,
        text = textWithEscapedCharacters().takeIf { photoUrl == null },
        caption = textWithEscapedCharacters().takeIf { photoUrl != null },
        parseMode = "MarkdownV2",
        replyMarkup = ReplyMarkupResponse(
            nestedButtons.map {
                it.map {
                    ButtonRequest(
                        text = it.text,
                        callbackData = "1",
                        url = it.url
                    )
                }
            }
        ),
        photo = photoUrl
    )
}