package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

interface MessageFactory {
    fun createDone(): TelegramMessageBody
    fun createNotification(notification: Notification): TelegramMessageBody
    fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody

    data class Notification(
        val totalCount: Int,
        val dataBases: List<DataBaseMessage>,
    )

    data class DataBaseMessage(
        val name: String,
        val countInt: Int,
    )
}