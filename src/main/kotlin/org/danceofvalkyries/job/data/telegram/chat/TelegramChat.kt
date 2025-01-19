package org.danceofvalkyries.job.data.telegram.chat

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.job.data.telegram.message.TelegramMessage
import org.danceofvalkyries.job.data.telegram.message.TelegramMessage.Button

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

    suspend fun edit(
        messageId: Long,
        newText: String,
        newNestedButtons: List<List<Button>>,
    ): TelegramMessage

    suspend fun getMessage(messageId: Long): TelegramMessage

    fun getEvents(): Flow<Button.Callback>
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

suspend fun TelegramChat.sendMessage(telegramMessage: TelegramMessage): TelegramMessage {
    return sendMessage(
        text = telegramMessage.text,
        imageUrl = telegramMessage.imageUrl,
        nestedButtons = telegramMessage.nestedButtons
    )
}