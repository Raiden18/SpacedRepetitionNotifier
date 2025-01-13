package org.danceofvalkyries.app.data.persistance.telegram.messages

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface TelegramMessagesDataBaseTable {
    suspend fun save(telegramMessage: TelegramMessage)
    suspend fun delete(telegramMessage: TelegramMessage)
    suspend fun getAll(): List<TelegramMessage>
    suspend fun update(telegramMessageBody: TelegramMessageBody, messageId: Long)
}