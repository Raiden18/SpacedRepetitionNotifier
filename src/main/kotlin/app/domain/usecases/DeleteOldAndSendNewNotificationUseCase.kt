package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.telegram.api.DeleteFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.impl.TelegramChatApi

fun interface DeleteOldAndSendNewNotificationUseCase {
    suspend fun execute(textBody: TelegramMessageBody)
}

fun DeleteOldAndSendNewNotificationUseCase(
    telegramChatApi: TelegramChatApi,
    deleteFromTelegramChat: DeleteFromTelegramChat,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
): DeleteOldAndSendNewNotificationUseCase {
    return DeleteOldAndSendNewNotificationUseCase {
        telegramChatApi.getAllFromDb()
            .filter { it.body.type == TelegramMessageBody.Type.NOTIFICATION }
            .forEach { oldMessage ->
                deleteFromTelegramChat.execute(oldMessage)
                telegramChatApi.deleteFromDb(oldMessage)
            }
        val telegramMessage = sendMessageToTelegramChat.execute(it)
        telegramChatApi.saveToDb(telegramMessage)
    }
}