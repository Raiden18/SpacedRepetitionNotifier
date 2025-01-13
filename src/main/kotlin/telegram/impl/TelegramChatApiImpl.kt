package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramUpdate
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

    override suspend fun getUpdates(): List<TelegramUpdate> {
        return api.getUpdates()
            .map {
                TelegramUpdate(
                    id = it.id,
                    telegramUpdateCallbackQuery = TelegramUpdateCallbackQuery(
                        id = it.callbackQueryData.id,
                        message = it.callbackQueryData.message.toDomain(),
                        callback = it.callbackQueryData.data,
                    ),
                )
            }
    }
}