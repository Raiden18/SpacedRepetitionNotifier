package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

data class TelegramMessageEntity(
    val id: Long,
    val text: String,
    val type: String
)