package org.danceofvalkyries.app.data.persistance.telegram.messages

import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDao
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageEntity
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class TelegramMessagesDataBaseTableImpl(
    private val db: TelegramMessageDao,
) : TelegramMessagesDataBaseTable {

    override suspend fun save(telegramMessage: TelegramMessage) {
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            text = telegramMessage.body.text.get(),
            type = telegramMessage.body.type.toString(),
        )
        db.save(entity)
    }

    override suspend fun delete(telegramMessage: TelegramMessage) {
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            text = telegramMessage.body.text.get(),
            type = telegramMessage.body.type.toString(),
        )
        db.delete(entity)
    }

    override suspend fun getAll(): List<TelegramMessage> {
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

    override suspend fun update(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        val entity = TelegramMessageEntity(
            id = messageId,
            text = telegramMessageBody.text.get(),
            type = telegramMessageBody.type.toString(),
        )
        db.update(entity, messageId)
    }
}