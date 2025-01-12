package org.danceofvalkyries.telegram.data.api

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface TelegramChatApi {
    suspend fun sendMessage(textBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteMessage(id: Long)
    suspend fun editMessageText(messageId: Long, text: TelegramMessageBody)
    suspend fun sendPhoto(textBody: TelegramMessageBody): TelegramMessage
    suspend fun getUpdate()
}
