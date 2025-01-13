package org.danceofvalkyries.telegram.domain

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface TelegramChatRepository {
    suspend fun sendToChat(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteFromChat(telegramMessage: TelegramMessage)
    suspend fun editInChat(telegramMessageBody: TelegramMessageBody, messageId: Long)

    // TODO: Move to app
    suspend fun saveToDb(telegramMessage: TelegramMessage)
    // TODO: Move to app
    suspend fun deleteFromDb(telegramMessage: TelegramMessage)
    // TODO: Move to app
    suspend fun getAllFromDb(): List<TelegramMessage>
    // TODO: Move to app
    suspend fun updateInDb(telegramMessageBody: TelegramMessageBody, messageId: Long)
}