package org.danceofvalkyries.telegram.api.models

data class TelegramUpdateCallbackQuery(
    val id: String,
    val message: TelegramMessage,
    val callback: String
)