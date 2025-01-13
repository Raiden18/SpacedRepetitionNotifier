package org.danceofvalkyries.telegram.api

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface SendMessageToTelegramChat {
    suspend fun execute(telegramMessageBody: TelegramMessageBody): TelegramMessage
}

fun interface DeleteFromTelegramChat {
    suspend fun execute(telegramMessage: TelegramMessage)
}

fun interface EditMessageInTelegramChat {
    suspend fun execute(telegramMessageBody: TelegramMessageBody, chatId: Long)
}