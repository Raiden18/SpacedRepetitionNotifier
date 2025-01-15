package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    telegramChatApi: TelegramChatApi,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
    telegramMessages: TelegramMessages,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramMessagesDataBaseTable.getMessagesIds().forEach { savedMessageId ->
            telegramMessagesDataBaseTable.deleteFor(savedMessageId)
            telegramChatApi.deleteFromChat(savedMessageId)
        }
        val telegramMessage = sendMessageToTelegramChat.execute(it.telegramBody)
        telegramMessages.add(
            id = telegramMessage.id,
            type = it.type
        )
    }
}