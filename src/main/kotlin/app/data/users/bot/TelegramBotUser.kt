package org.danceofvalkyries.app.data.users.bot

import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.sendMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.app.data.users.User
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.telegram.api.models.TelegramButton

class TelegramBotUser(
    private val telegramChat: TelegramChat,
    private val notionDataBases: NotionDataBases,
    private val telegramMessagesType: TelegramMessagesType,
) : User {

    suspend fun editOldNotificationMessageToDoneMessage() {
        val doneMessage = DoneMessage()
        telegramMessagesType.iterate().forEach { message ->
            val message = telegramChat.getMessage(messageId = message.id)
            message.edit(
                newText = doneMessage.asTelegramBody().text.get(),
                newImageUrl = doneMessage.asTelegramBody().imageUrl?.get(),
                newNestedButtons = doneMessage.asTelegramBody().nestedButtons.map {
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
    }

    suspend fun deleteOldNotificationMessage() {
        telegramMessagesType.iterate().forEach {
            telegramMessagesType.delete(it.id)
            telegramChat.delete(messageId = it.id)
        }
    }

    suspend fun sendNewNotificationMessage() {
        val notificationMessage = NeedRevisingNotificationMessage(notionDataBases)
        val sentMessage = telegramChat.sendMessage(
            text = notificationMessage.asTelegramBody().text.get(),
            imageUrl = notificationMessage.asTelegramBody().imageUrl?.get(),
            nestedButtons = notificationMessage.asTelegramBody().nestedButtons.map {
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
        telegramMessagesType.add(
            id = sentMessage.id,
            type = notificationMessage.type
        )
    }
}