package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

fun interface EditNotificationMessageUseCase {
    suspend fun execute(text: TelegramMessageBody)
}

fun EditNotificationMessageUseCase(
    telegramChatRepository: TelegramChatRepository
): EditNotificationMessageUseCase {
    return EditNotificationMessageUseCase {
        telegramChatRepository.getAllFromDb().forEach { message ->
            telegramChatRepository.updateInDb(it.text, message.id)
            telegramChatRepository.editInChat(it.text, message.id)
        }
    }
}