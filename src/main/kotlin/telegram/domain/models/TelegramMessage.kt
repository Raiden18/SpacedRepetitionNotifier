package org.danceofvalkyries.telegram.domain.models

data class TelegramMessage(
    val id: Long,
    val body: TelegramMessageBody
)