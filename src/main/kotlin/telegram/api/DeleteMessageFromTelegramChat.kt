package org.danceofvalkyries.telegram.api

import org.danceofvalkyries.telegram.api.models.TelegramMessage

fun interface DeleteMessageFromTelegramChat {
    suspend fun execute(telegramMessage: TelegramMessage)
}