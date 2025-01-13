package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.telegram.api.EditMessageInTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface EditNotificationMessageUseCase {
    suspend fun execute(text: TelegramMessageBody)
}

fun EditNotificationMessageUseCase(
    telegramChatApi: TelegramChatApi,
    editMessageInTelegramChat: EditMessageInTelegramChat,
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramChatApi.getAllFromDb().forEach { message ->
            telegramChatApi.updateInDb(it, message.id)
            editMessageInTelegramChat.execute(it, message.id)
        }
    }
}