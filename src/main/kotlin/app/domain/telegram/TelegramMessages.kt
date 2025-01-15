package org.danceofvalkyries.app.domain.telegram

interface TelegramMessages {
    suspend fun iterate(): Sequence<TelegramMessage>
    suspend fun add(id: Long, type: String): TelegramMessage
    suspend fun delete(id: Long)
}