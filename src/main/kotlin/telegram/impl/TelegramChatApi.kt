package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface TelegramChatApi {
    suspend fun sendTextMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun sendPhotoMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteFromChat(messageId: Long)
    suspend fun editInChat(telegramMessageBody: TelegramMessageBody, messageId: Long)
}