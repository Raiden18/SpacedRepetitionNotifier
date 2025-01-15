package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.TelegramChatApi

class TelegramNotificationView(
    private val notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
    private val notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    private val telegramChatApi: TelegramChatApi,
    private val messages: TelegramMessages,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = messages.iterate().first { it.type == NotificationMessage.TYPE_NAME }
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(
                flashCards = notionDatabaseDataBaseTable.getAll()
                    .map { it.id }
                    .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) },
                notionDataBases = notionDatabaseDataBaseTable.getAll()
            ).telegramBody,
            notificationMessage.id
        )
    }

    override suspend fun hide() {

    }
}