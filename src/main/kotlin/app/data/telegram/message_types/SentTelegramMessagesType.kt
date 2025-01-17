package org.danceofvalkyries.app.data.telegram.message_types

interface SentTelegramMessagesType {
    suspend fun iterate(): Sequence<SentTelegramMessageType>
    suspend fun add(id: Long, type: String): SentTelegramMessageType
    suspend fun delete(id: Long)
}