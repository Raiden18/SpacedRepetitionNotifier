package org.danceofvalkyries.telegram.api

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramUpdate
import org.danceofvalkyries.telegram.api.models.TelegramUpdateCallbackQuery

interface TelegramChatApi {
    suspend fun sendTextMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun sendPhotoMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteFromChat(messageId: Long)
    suspend fun editInChat(telegramMessageBody: TelegramMessageBody, messageId: Long)
    suspend fun getUpdates(): Flow<TelegramUpdate>
    suspend fun answerCallbackQuery(telegramUpdateCallbackQuery: TelegramUpdateCallbackQuery)
}