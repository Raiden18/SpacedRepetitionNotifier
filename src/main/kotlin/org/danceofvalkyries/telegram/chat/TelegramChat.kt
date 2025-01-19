package org.danceofvalkyries.telegram.chat

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.telegram.message.TelegramMessage

interface TelegramChat {

    suspend fun sendTextMessage(
        text: String,
        nestedButtons: List<List<TelegramMessage.Button>>,
    ): TelegramMessage

    suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage

    suspend fun delete(messageId: Long)

    suspend fun edit(
        messageId: Long,
        newText: String,
        newNestedButtons: List<List<TelegramMessage.Button>>,
    ): TelegramMessage

    suspend fun getMessage(messageId: Long): TelegramMessage

    fun getEvents(): Flow<TelegramMessage.Button.Callback>
}

suspend fun TelegramChat.sendMessage(
    text: String,
    imageUrl: String?,
    nestedButtons: List<List<TelegramMessage.Button>>
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

suspend fun TelegramChat.sendMessage(telegramMessage: TelegramMessage): TelegramMessage {
    return sendMessage(
        text = telegramMessage.text,
        imageUrl = telegramMessage.imageUrl,
        nestedButtons = telegramMessage.nestedButtons
    )
}