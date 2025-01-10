package org.danceofvalkyries.telegram.domain

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface TelegramChatRepository {
    suspend fun sendToChat(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteFromChat(telegramMessage: TelegramMessage)
    suspend fun saveToDb(telegramMessage: TelegramMessage)
    suspend fun deleteFromDb(telegramMessage: TelegramMessage)
}