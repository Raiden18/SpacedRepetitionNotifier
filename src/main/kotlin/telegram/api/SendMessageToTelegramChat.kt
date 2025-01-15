package org.danceofvalkyries.telegram.api

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface SendMessageToTelegramChat {
    suspend fun execute(telegramMessageBody: TelegramMessageBody): TelegramMessage
}