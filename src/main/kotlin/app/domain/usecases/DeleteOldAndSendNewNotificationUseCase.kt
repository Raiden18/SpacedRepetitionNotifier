package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(textBody: TelegramMessageBody)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramChatRepository: TelegramChatRepository,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramChatRepository.getAllFromDb().forEach { oldMessage ->
            telegramChatRepository.deleteFromChat(oldMessage)
            telegramChatRepository.deleteFromDb(oldMessage)
        }
        val telegramMessage = telegramChatRepository.sendToChat(it)
        telegramChatRepository.saveToDb(telegramMessage)
    }
}