package org.danceofvalkyries.app.data.telegram.chat.restful

import kotlinx.coroutines.flow.Flow

interface KtorWebServer {
    fun getWebHook(): Flow<String>
}