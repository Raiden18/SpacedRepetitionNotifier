package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.models.TelegramMessage

interface TelegramNotificationMessageDb {
    suspend fun save(telegramMessage: TelegramMessage)
    suspend fun delete(oldTelegramMessage: TelegramMessage)
    suspend fun update(text: String, messageId: Long)
    suspend fun getAll(): List<TelegramMessage>
}