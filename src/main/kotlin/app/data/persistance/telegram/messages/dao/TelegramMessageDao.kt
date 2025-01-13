package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

interface TelegramMessageDao {
    suspend fun save(entity: TelegramMessageEntity)
    suspend fun delete(entity: TelegramMessageEntity)
    suspend fun update(entity: TelegramMessageEntity, messageId: Long)
    suspend fun getAll(): List<TelegramMessageEntity>
}