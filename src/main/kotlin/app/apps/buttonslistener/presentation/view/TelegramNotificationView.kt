package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.TelegramChatApi

class TelegramNotificationView(
    private val notionDataBases: NotionDataBases,
    private val telegramChatApi: TelegramChatApi,
    private val messages: TelegramMessages,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = messages.iterate().first { it.type == NotificationMessage.TYPE_NAME }
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(notionDataBases).asTelegramBody(),
            notificationMessage.id
        )
    }

    override suspend fun hide() {

    }
}