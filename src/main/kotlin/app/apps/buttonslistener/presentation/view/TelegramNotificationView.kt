package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.telegram.api.TelegramChatApi

class TelegramNotificationView(
    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    private val notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
    private val notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    private val telegramChatApi: TelegramChatApi,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = telegramMessagesDataBaseTable.getMessagesIds().first { telegramMessagesDataBaseTable.getTypeFor(it) == NotificationMessage.TYPE_NAME }
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(
                flashCards = notionDatabaseDataBaseTable.getAll()
                    .map { it.id }
                    .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) },
                notionDataBases = notionDatabaseDataBaseTable.getAll()
            ).telegramBody,
            notificationMessage
        )
    }

    override suspend fun hide() {

    }
}