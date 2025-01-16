package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface Message {
    val id: Long
    val type: String

    suspend fun asTelegramBody(): TelegramMessageBody
}