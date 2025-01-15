package org.danceofvalkyries.telegram.impl

import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

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
