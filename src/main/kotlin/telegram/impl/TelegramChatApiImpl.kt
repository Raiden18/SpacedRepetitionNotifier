package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramUpdateCallbackQuery
import org.danceofvalkyries.telegram.impl.client.TelegramChatRestApi

class TelegramChatApiImpl(
    private val api: TelegramChatRestApi,
    private val chatId: String,
) : TelegramChatApi {

    override suspend fun sendTextMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = api.sendMessage(request)
        return TelegramMessage(
            id = response.messageId!!,
            body = telegramMessageBody
        )
    }

    override suspend fun sendPhotoMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = api.sendPhoto(request)
        return TelegramMessage(
            id = response.messageId!!,
            body = telegramMessageBody
        )
    }

    override suspend fun deleteFromChat(messageId: Long) {
        api.deleteMessage(messageId, chatId.toLong())
    }

    override suspend fun editInChat(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        val request = telegramMessageBody.toRequest(
            chatId = chatId,
            messageId = messageId,
        )
        api.editMessageText(messageId, request)
    }

    override suspend fun getUpdates(): List<TelegramUpdateCallbackQuery> {
        return api.getUpdates()
            .map { it.callbackQueryData }
            .map {
                TelegramUpdateCallbackQuery(
                    id = it.id,
                    messages = it.message.toDomain(),
                    callback = it.data
                )
            }
    }
}