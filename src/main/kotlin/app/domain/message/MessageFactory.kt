package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(group: SpacedRepetitionDataBaseGroup): TelegramMessageBody
    fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody
}