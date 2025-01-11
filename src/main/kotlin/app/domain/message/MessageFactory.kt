package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(group: FlashCardsTablesGroup): TelegramMessageBody
    fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody
}