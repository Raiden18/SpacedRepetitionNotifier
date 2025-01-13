package org.danceofvalkyries.app.data.repositories.telegram.db

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface TelegramNotificationMessageDb {
    suspend fun save(telegramMessage: TelegramMessage)
    suspend fun delete(oldTelegramMessage: TelegramMessage)
    suspend fun update(text: TelegramMessageBody, messageId: Long)
    suspend fun getAll(): List<TelegramMessage>
}