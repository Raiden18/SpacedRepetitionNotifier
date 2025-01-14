package org.danceofvalkyries.app.data.persistance.telegram.messages

import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDao
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageEntity
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class TelegramMessagesDataBaseTableImpl(
    private val db: TelegramMessageDao,
) : TelegramMessagesDataBaseTable {

    override suspend fun save(telegramMessage: TelegramMessage, type: String) {
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            type = type
        )
        db.save(entity)
    }

    override suspend fun delete(telegramMessage: TelegramMessage) {
        db.delete(telegramMessage.id)
    }

    override suspend fun getAll(): List<TelegramMessage> {
        return db.getAll()
            .map {
                TelegramMessage(
                    id = it.id,
                    body = TelegramMessageBody(
                        text = "",
                        telegramButtons = emptyList(),
                        telegramImageUrl = null,
                    )
                )
            }
    }

    override suspend fun update(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        val entity = TelegramMessageEntity(
            id = messageId,
            type = null
        )
        db.update(entity, messageId)
    }

    override suspend fun deleteFor(id: Long) {
        db.delete(id)
    }

    override suspend fun getMessagesIds(): List<Long> {
        return db.getAll().map { it.id }
    }

    override suspend fun getTypeFor(messageId: Long): String? {
        return db.getBy(messageId)?.type
    }
}