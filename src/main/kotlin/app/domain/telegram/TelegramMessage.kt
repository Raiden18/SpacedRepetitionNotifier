package org.danceofvalkyries.app.domain.telegram

interface TelegramMessage {
    val id: Long
    val type: String
}