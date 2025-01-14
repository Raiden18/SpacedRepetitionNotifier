package org.danceofvalkyries.telegram.impl.client

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.telegram.impl.client.models.MessageData
import org.danceofvalkyries.telegram.impl.client.models.UpdateResponseData

interface TelegramChatRestApi {
    suspend fun sendMessage(messageData: MessageData): MessageData
    suspend fun deleteMessage(messageId: Long, chatId: Long)
    suspend fun editMessageText(messageId: Long, text: MessageData)
    suspend fun sendPhoto(messageData: MessageData): MessageData
    suspend fun getUpdates(): Flow<UpdateResponseData>
}
