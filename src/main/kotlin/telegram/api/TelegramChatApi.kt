package org.danceofvalkyries.telegram.api

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.telegram.api.models.TelegramUpdate

interface TelegramChatApi {
    suspend fun getUpdates(): Flow<TelegramUpdate>
}