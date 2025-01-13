package org.danceofvalkyries.telegram.impl.client

import org.danceofvalkyries.telegram.impl.client.request.bodies.SendMessageRequest
import org.danceofvalkyries.telegram.impl.client.response.TelegramMessageResponse

interface TelegramChatRestApi {
    suspend fun sendMessage(textBody: SendMessageRequest): TelegramMessageResponse
    suspend fun deleteMessage(messageId: Long, chatId: Long)
    suspend fun editMessageText(messageId: Long, text: SendMessageRequest)
    suspend fun sendPhoto(textBody: SendMessageRequest): TelegramMessageResponse
    suspend fun getUpdate()
}
