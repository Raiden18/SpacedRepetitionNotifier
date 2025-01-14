package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(notificationMessage: NotificationMessage)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    telegramChatApi: TelegramChatApi,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramMessagesDataBaseTable.getMessagesIds().forEach { savedMessageId ->
            telegramMessagesDataBaseTable.deleteFor(savedMessageId)
            telegramChatApi.deleteFromChat(savedMessageId)
        }
        val telegramMessage = sendMessageToTelegramChat.execute(it.telegramBody)
        telegramMessagesDataBaseTable.save(telegramMessage, it.type)
    }
}