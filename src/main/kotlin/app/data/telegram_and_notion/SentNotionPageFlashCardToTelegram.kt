package org.danceofvalkyries.app.data.telegram_and_notion

interface SentNotionPageFlashCardToTelegram {
    val messageId: Long
    val notionPageId: String
}