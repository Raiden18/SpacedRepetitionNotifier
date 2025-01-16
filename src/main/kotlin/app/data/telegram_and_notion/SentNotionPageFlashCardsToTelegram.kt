package org.danceofvalkyries.app.data.telegram_and_notion

interface SentNotionPageFlashCardsToTelegram {
    fun iterate(): Sequence<SentNotionPageFlashCardToTelegram>
    fun add(telegramMessageId: Long, notionPageId: String): SentNotionPageFlashCardToTelegram
    fun delete(telegramMessageId: Long)
}