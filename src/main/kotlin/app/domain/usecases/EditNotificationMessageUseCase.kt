package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.telegram.api.EditMessageInTelegramChat

fun interface EditNotificationMessageUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun EditNotificationMessageUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    editMessageInTelegramChat: EditMessageInTelegramChat,
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramMessagesDataBaseTable.getAll().forEach { message ->
            telegramMessagesDataBaseTable.update(it.telegramBody, message.id)
            editMessageInTelegramChat.execute(it.telegramBody, message.id)
        }
    }
}