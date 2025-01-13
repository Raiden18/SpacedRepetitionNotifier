package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.telegram.api.EditMessageInTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface EditNotificationMessageUseCase {
    suspend fun execute(text: TelegramMessageBody)
}

fun EditNotificationMessageUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    editMessageInTelegramChat: EditMessageInTelegramChat,
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramMessagesDataBaseTable.getAll().forEach { message ->
            telegramMessagesDataBaseTable.update(it, message.id)
            editMessageInTelegramChat.execute(it, message.id)
        }
    }
}