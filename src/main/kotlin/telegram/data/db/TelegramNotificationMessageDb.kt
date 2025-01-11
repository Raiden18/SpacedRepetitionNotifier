package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface TelegramNotificationMessageDb {
    suspend fun save(telegramMessage: TelegramMessage)
    suspend fun delete(oldTelegramMessage: TelegramMessage)
    suspend fun update(text: TelegramMessageBody, messageId: Long)
    suspend fun getAll(): List<TelegramMessage>
}