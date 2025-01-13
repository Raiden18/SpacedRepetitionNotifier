package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.telegram.api.DeleteMessageFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(textBody: TelegramMessageBody)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    deleteMessageFromTelegramChat: DeleteMessageFromTelegramChat,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramMessagesDataBaseTable.getAll()
            .filter { it.body.type == TelegramMessageBody.Type.NOTIFICATION }
            .forEach { oldMessage ->
                deleteMessageFromTelegramChat.execute(oldMessage)
                telegramMessagesDataBaseTable.delete(oldMessage)
            }
        val telegramMessage = sendMessageToTelegramChat.execute(it)
        telegramMessagesDataBaseTable.save(telegramMessage)
    }
}