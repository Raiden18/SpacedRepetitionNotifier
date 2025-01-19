package org.danceofvalkyries.telegram.message_types

import org.danceofvalkyries.telegram.message.TelegramMessage

interface SentTelegramMessageType : TelegramMessage {
    val type: String
}

suspend fun SentTelegramMessageType.saveTo(sentTelegramMessagesType: SentTelegramMessagesType) {
    sentTelegramMessagesType.add(
        id = id,
        type = type
    )
}

suspend fun SentTelegramMessageType.deleteFrom(sentTelegramMessagesType: SentTelegramMessagesType) {
    sentTelegramMessagesType.delete(id)
}
