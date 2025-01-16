package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.TelegramChatApi

fun interface EditNotificationMessageUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun EditNotificationMessageUseCase(
    telegramMessages: TelegramMessages,
    telegramChatApi: TelegramChatApi,
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramMessages.iterate().forEach { message ->
            telegramChatApi.editInChat(it.asTelegramBody(), message.id)
        }
    }
}