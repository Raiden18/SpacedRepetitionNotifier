package org.danceofvalkyries.telegram.api

import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface EditMessageInTelegramChat {
    suspend fun execute(telegramMessageBody: TelegramMessageBody, chatId: Long)
}