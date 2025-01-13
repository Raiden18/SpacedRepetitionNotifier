package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(
        flashCards: List<FlashCard>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody

    fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody
}