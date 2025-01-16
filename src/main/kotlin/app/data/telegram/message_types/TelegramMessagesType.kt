package org.danceofvalkyries.app.data.telegram.message_types

interface TelegramMessagesType {
    suspend fun iterate(): Sequence<TelegramMessageType>
    suspend fun add(id: Long, type: String): TelegramMessageType
    suspend fun delete(id: Long)
}