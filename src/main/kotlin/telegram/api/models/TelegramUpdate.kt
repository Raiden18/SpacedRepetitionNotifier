package org.danceofvalkyries.telegram.api.models

data class TelegramUpdate(
    val id: Long,
    val telegramUpdateCallbackQuery: TelegramUpdateCallbackQuery,
)