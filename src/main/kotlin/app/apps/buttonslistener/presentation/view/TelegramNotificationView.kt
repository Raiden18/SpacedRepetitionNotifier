package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.notion.NotionDataBases
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.TelegramChatApi

class TelegramNotificationView(
    private val notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
    private val notionDataBases: NotionDataBases,
    private val telegramChatApi: TelegramChatApi,
    private val messages: TelegramMessages,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = messages.iterate().first { it.type == NotificationMessage.TYPE_NAME }
        val flashCards = notionDataBases.iterate()
            .map { it.id }
            .map { NotionId(it) }
            .toList()
            .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) }
            .toList()
        val notionDataBases = notionDataBases.iterate()
            .map {
                NotionDataBase(
                    id = NotionId(it.id),
                    name = it.name
                )
            }.toList()
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(
                flashCards = flashCards,
                notionDataBases = notionDataBases
            ).telegramBody,
            notificationMessage.id
        )
    }

    override suspend fun hide() {

    }
}