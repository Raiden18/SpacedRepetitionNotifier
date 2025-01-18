package org.danceofvalkyries.app.data.telegram.users.bot.messages

import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType

class SerializedMessage(
    override val id: Long,
    override val type: String,
) : LocalTelegramMessage() {

    override val text: String
        get() = error("Text was not saved")

    suspend fun saveTo(sentTelegramMessagesType: SentTelegramMessagesType) {
        sentTelegramMessagesType.add(
            id = id,
            type = type
        )
    }

    suspend fun deleteFrom(sentTelegramMessagesType: SentTelegramMessagesType) {
        sentTelegramMessagesType.delete(id)
    }
}