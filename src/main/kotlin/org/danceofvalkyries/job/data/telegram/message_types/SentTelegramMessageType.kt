package org.danceofvalkyries.job.data.telegram.message_types

import org.danceofvalkyries.job.data.telegram.message.TelegramMessage

interface SentTelegramMessageType : TelegramMessage {
    val type: String
}

suspend fun org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType.saveTo(sentTelegramMessagesType: org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessagesType) {
    sentTelegramMessagesType.add(
        id = id,
        type = type
    )
}

suspend fun org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType.deleteFrom(sentTelegramMessagesType: org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessagesType) {
    sentTelegramMessagesType.delete(id)
}
