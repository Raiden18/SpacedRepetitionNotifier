package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.app.data.persistance.telegram.TelegramMessageDao
import org.danceofvalkyries.app.data.persistance.telegram.TelegramMessageEntity
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.impl.restapi.TelegramChatRestApi

class TelegramChatApiImpl(
    private val api: TelegramChatRestApi,
    private val db: TelegramMessageDao,
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
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            text = telegramMessage.body.text.get(),
            type = telegramMessage.body.type.toString(),

            )
        db.save(entity)
    }

    override suspend fun deleteFromDb(telegramMessage: TelegramMessage) {
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            text = telegramMessage.body.text.get(),
            type = telegramMessage.body.type.toString(),
        )
        db.delete(entity)
    }

    override suspend fun getAllFromDb(): List<TelegramMessage> {
        return db.getAll()
            .map {
                TelegramMessage(
                    id = it.id,
                    body = TelegramMessageBody(
                        text = it.text,
                        telegramButtons = emptyList(),
                        telegramImageUrl = null,
                        type = TelegramMessageBody.Type.valueOf(it.type)
                    )
                )
            }
    }

    override suspend fun updateInDb(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        val entity = TelegramMessageEntity(
            id = messageId,
            text = telegramMessageBody.text.get(),
            type = telegramMessageBody.type.toString(),
        )
        db.update(entity, messageId)
    }
}