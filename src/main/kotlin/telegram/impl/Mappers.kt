package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.models.*
import org.danceofvalkyries.telegram.impl.client.models.ButtonData
import org.danceofvalkyries.telegram.impl.client.models.MessageData
import org.danceofvalkyries.telegram.impl.client.models.ReplyMarkupData

// TODO: Add Unit tests when I've figured out what to do with callbackData
fun TelegramMessageBody.toRequest(
    chatId: String,
    messageId: Long? = null,
): MessageData {
    return MessageData(
        chatId = chatId,
        text = text.get().takeIf { imageUrl == null },
        caption = text.get().takeIf { imageUrl != null },
        parseMode = "MarkdownV2",
        replyMarkup = ReplyMarkupData(
            nestedButtons.map {
                it.map {
                    ButtonData(
                        text = it.text,
                        callbackData = it.callback,
                        url = it.url
                    )
                }
            }
        ),
        photo = imageUrl?.get(),
        messageId = messageId
    )
}

fun MessageData.toDomain(): TelegramMessage {
    val imageUrl = photo
    val text = if (imageUrl == null) text!! else caption!!
    return TelegramMessage(
        id = messageId!!,
        body = TelegramMessageBody(
            text = TelegramText(text),
            nestedButtons = replyMarkup
                ?.inlineKeyboards
                ?.map {
                    it.map {
                        TelegramButton(
                            text = it.text,
                            url = it.url.orEmpty(),
                            callback = it.callbackData
                        )
                    }
                } ?: emptyList(),
            imageUrl = imageUrl?.let(::TelegramImageUrl),
        )
    )
}