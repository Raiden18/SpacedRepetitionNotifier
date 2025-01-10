package org.danceofvalkyries.telegram.data.repositories

import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class TelegramChatRepositoryImpl(
    private val api: TelegramChatApi,
    private val db: TelegramNotificationMessageDb,
) : TelegramChatRepository {

    override suspend fun sendToChat(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        return if (telegramMessageBody.photoUrl == null) {
            api.sendMessage(telegramMessageBody)
        } else {
            api.sendPhoto(telegramMessageBody)
        }
    }

    override suspend fun deleteFromChat(telegramMessage: TelegramMessage) {
        api.deleteMessage(telegramMessage.id)
    }

    override suspend fun editInChat(text: String, messageId: Long) {
        api.editMessageText(messageId, text)
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

    override suspend fun updateInDb(text: String, messageId: Long) {
        db.update(text, messageId)
    }
}