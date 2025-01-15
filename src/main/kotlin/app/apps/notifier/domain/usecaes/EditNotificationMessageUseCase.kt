package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.telegram.api.TelegramChatApi

fun interface EditNotificationMessageUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun EditNotificationMessageUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    telegramChatApi: TelegramChatApi,
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramMessagesDataBaseTable.getAll().forEach { message ->
            telegramMessagesDataBaseTable.update(it.telegramBody, message.id)
            telegramChatApi.editInChat(it.telegramBody, message.id)
        }
    }
}