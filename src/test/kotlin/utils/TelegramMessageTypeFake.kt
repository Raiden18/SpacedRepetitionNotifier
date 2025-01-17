package utils

import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessageType
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType

 class TelegramMessageTypeFake() : TelegramMessagesType {
    override suspend fun iterate(): Sequence<TelegramMessageType> {
        TODO("Not yet implemented")
    }

    override suspend fun add(id: Long, type: String): TelegramMessageType {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}