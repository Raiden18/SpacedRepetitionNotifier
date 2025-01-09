package org.danceofvalkyries.telegram.domain

data class TelegramMessage(
    val id: Long,
    val body: TelegramMessageBody
)