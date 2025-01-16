package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.telegram.api.models.TelegramButton

class TelegramNotificationView(
    private val telegramBotUser: TelegramBotUser,
) : NotificationView {

    override suspend fun update() {

    }

    override suspend fun hide() {

    }
}