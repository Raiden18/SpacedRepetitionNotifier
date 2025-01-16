package org.danceofvalkyries.app.data.telegram

interface TelegramMessages {
    suspend fun iterate(): Sequence<TelegramMessage>
    suspend fun add(id: Long, type: String): TelegramMessage
    suspend fun delete(id: Long)
}