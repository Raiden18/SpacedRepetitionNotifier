package org.danceofvalkyries.telegram.api.models

data class TelegramUpdateCallbackQuery(
    val id: String,
    val messages: TelegramMessage,
    val callback: String
)