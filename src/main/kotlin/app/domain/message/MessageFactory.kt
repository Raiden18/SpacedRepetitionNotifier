package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(
        flashCards: List<FlashCard>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody

    fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody
}