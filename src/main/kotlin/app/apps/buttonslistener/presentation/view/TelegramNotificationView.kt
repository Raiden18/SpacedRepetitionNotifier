package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.telegram.api.models.TelegramButton

class TelegramNotificationView(
    private val notionDataBases: NotionDataBases,
    private val telegramChat: TelegramChat,
    private val messages: TelegramMessagesType,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = messages.iterate().first { it.type == NotificationMessage.TYPE_NAME }
        telegramChat.getMessage(notificationMessage.id)
            .edit(
                newText = NeedRevisingNotificationMessage(notionDataBases).asTelegramBody().text.get(),
                newImageUrl = NeedRevisingNotificationMessage(notionDataBases).asTelegramBody().imageUrl?.get(),
                newNestedButtons = NeedRevisingNotificationMessage(notionDataBases).asTelegramBody().nestedButtons.map {
                    it.map { button ->
                        object : TelegramMessage.Button {
                            override val text: String
                                get() = button.text
                            override val action: TelegramMessage.Button.Action
                                get() = when (val action = button.action) {
                                    is TelegramButton.Action.Url -> TelegramMessage.Button.Action.Url(action.value)
                                    is TelegramButton.Action.CallBackData -> TelegramMessage.Button.Action.CallBackData(action.value)
                                }

                        }
                    }
                }
            )
    }

    override suspend fun hide() {

    }
}