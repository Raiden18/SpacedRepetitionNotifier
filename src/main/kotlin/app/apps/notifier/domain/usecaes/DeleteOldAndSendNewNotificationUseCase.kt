package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramChatApi: TelegramChatApi,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
    telegramMessages: TelegramMessages,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramMessages.iterate().forEach {
            telegramMessages.delete(it.id)
            telegramChatApi.deleteFromChat(it.id)
        }
        val telegramMessage = sendMessageToTelegramChat.execute(it.telegramBody)
        telegramMessages.add(
            id = telegramMessage.id,
            type = it.type
        )
    }
}