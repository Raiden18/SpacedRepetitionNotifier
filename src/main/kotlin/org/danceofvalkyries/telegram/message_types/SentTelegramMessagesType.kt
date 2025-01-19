package org.danceofvalkyries.telegram.message_types

import kotlinx.coroutines.flow.Flow

interface SentTelegramMessagesType {
    suspend fun iterate(): Flow<SentTelegramMessageType>
    suspend fun iterate(type: String): Flow<SentTelegramMessageType>
    suspend fun add(id: Long, type: String): SentTelegramMessageType
    suspend fun delete(id: Long)
}