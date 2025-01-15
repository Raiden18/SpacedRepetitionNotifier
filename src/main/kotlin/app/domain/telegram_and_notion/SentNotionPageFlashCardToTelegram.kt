package org.danceofvalkyries.app.domain.telegram_and_notion

interface SentNotionPageFlashCardToTelegram {
    val messageId: Long
    val notionPageId: String
}