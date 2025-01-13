package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.app.data.repositories.telegram.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.impl.restapi.TelegramChatRestApi

class TelegramChatApiImpl(
    private val api: TelegramChatRestApi,
    private val db: TelegramNotificationMessageDb,
    private val chatId: String,
) : TelegramChatApi {

    override suspend fun sendTextMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = api.sendMessage(request)
        return TelegramMessage(
            id = response.messageId,
            body = telegramMessageBody
        )
    }

    override suspend fun sendPhotoMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = api.sendPhoto(request)
        return TelegramMessage(
            id = response.messageId,
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

    override suspend fun saveToDb(telegramMessage: TelegramMessage) {
        db.save(telegramMessage)
    }

    override suspend fun deleteFromDb(telegramMessage: TelegramMessage) {
        db.delete(telegramMessage)
    }

    override suspend fun getAllFromDb(): List<TelegramMessage> {
        return db.getAll()
    }

    override suspend fun updateInDb(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        db.update(telegramMessageBody, messageId)
    }
}