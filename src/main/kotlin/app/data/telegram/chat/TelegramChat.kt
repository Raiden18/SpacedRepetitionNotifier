package org.danceofvalkyries.app.data.telegram.chat

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage.Button

interface TelegramChat {

    suspend fun sendTextMessage(
        text: String,
        nestedButtons: List<List<Button>>,
    ): TelegramMessage

    suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<Button>>
    ): TelegramMessage

    suspend fun delete(messageId: Long)

    suspend fun getMessage(messageId: Long): TelegramMessage
}

suspend fun TelegramChat.sendMessage(
    text: String,
    imageUrl: String?,
    nestedButtons: List<List<Button>>
): TelegramMessage {
    return if (imageUrl == null) {
        sendTextMessage(
            text = text,
            nestedButtons = nestedButtons,
        )
    } else {
        sendPhotoMessage(
            caption = text,
            imageUrl = imageUrl,
            nestedButtons = nestedButtons,
        )
    }
}