package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.impl.restapi.request.bodies.ButtonRequest
import org.danceofvalkyries.telegram.impl.restapi.request.bodies.ReplyMarkupResponse
import org.danceofvalkyries.telegram.impl.restapi.request.bodies.SendMessageRequest
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

// TODO: Add Unit tests when I've figured out what to do with callbackData
fun TelegramMessageBody.toRequest(
    chatId: String,
    messageId: Long? = null,
): SendMessageRequest {
    return SendMessageRequest(
        chatId = chatId,
        text = text.get().takeIf { imageUrl == null },
        caption = text.get().takeIf { imageUrl != null },
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
        photo = imageUrl?.get(),
        messageId = messageId
    )
}
