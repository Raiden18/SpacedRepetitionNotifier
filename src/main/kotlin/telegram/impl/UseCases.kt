package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.DeleteFromTelegramChat
import org.danceofvalkyries.telegram.api.EditMessageInTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat

fun SendMessageToTelegramChat(
    telegramChatApi: TelegramChatApi
): SendMessageToTelegramChat {
    return SendMessageToTelegramChat { telegramMessageBody ->
        return@SendMessageToTelegramChat if (telegramMessageBody.imageUrl == null) {
            telegramChatApi.sendTextMessage(telegramMessageBody)
        } else {
            telegramChatApi.sendPhotoMessage(telegramMessageBody)
        }
    }
}

fun DeleteFromTelegramChat(telegramChatApi: TelegramChatApi): DeleteFromTelegramChat {
    return DeleteFromTelegramChat { telegramChatApi.deleteFromChat(it.id) }
}

fun EditMessageInTelegramChat(telegramChatApi: TelegramChatApi): EditMessageInTelegramChat {
    return EditMessageInTelegramChat { body, messageId -> telegramChatApi.editInChat(body, messageId) }
}