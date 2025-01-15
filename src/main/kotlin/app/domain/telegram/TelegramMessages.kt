package org.danceofvalkyries.app.domain.telegram

interface TelegramMessages {
    fun iterate(): Sequence<TelegramMessage>
    fun add(id: Long, type: String): TelegramMessage
}