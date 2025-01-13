package org.danceofvalkyries.app.data.persistance.telegram

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface TelegramMessageDao {
    suspend fun save(telegramMessage: TelegramMessage)
    suspend fun delete(oldTelegramMessage: TelegramMessage)
    suspend fun update(text: TelegramMessageBody, messageId: Long)
    suspend fun getAll(): List<TelegramMessage>
}