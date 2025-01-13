package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(
        flashCards: List<FlashCardNotionPage>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody

    fun createFlashCardMessage(flashCard: FlashCardNotionPage): TelegramMessageBody
}