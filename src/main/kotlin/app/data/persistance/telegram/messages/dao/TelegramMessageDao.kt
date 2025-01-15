package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

interface TelegramMessageDao {
    suspend fun save(entity: TelegramMessageEntity)
    suspend fun delete(id: Long)
    suspend fun update(entity: TelegramMessageEntity, messageId: Long)
    suspend fun getAll(): List<TelegramMessageEntity>
    suspend fun getBy(id: Long): TelegramMessageEntity?
}