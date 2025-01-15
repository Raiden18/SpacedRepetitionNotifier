package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.models.*
import org.danceofvalkyries.telegram.impl.models.ButtonData
import org.danceofvalkyries.telegram.impl.models.MessageData
import org.danceofvalkyries.telegram.impl.models.ReplyMarkupData

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
                it.map { button ->
                    ButtonData(
                        text = button.text,
                        callbackData = button.action.value.takeIf { button.action is TelegramButton.Action.CallBackData },
                        url = button.action.value.takeIf { button.action is TelegramButton.Action.Url }
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
                        val action = if (it.url != null) {
                            TelegramButton.Action.Url(it.url)
                        } else {
                            TelegramButton.Action.CallBackData(it.callbackData!!)

                        }
                        TelegramButton(
                            text = it.text,
                            action = action,
                        )
                    }
                } ?: emptyList(),
            imageUrl = imageUrl?.let(::TelegramImageUrl),
        )
    )
}