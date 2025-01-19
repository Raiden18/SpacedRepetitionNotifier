package utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import org.danceofvalkyries.telegram.message.local.SerializedMessage
import org.danceofvalkyries.telegram.message_types.SentTelegramMessageType
import org.danceofvalkyries.telegram.message_types.SentTelegramMessagesType


class SentTelegramMessagesTypeFake(
    private var telegramMessages: List<SentTelegramMessageType> = emptyList()
) : SentTelegramMessagesType {

    override suspend fun iterate(): Flow<SentTelegramMessageType> {
        return telegramMessages.asFlow()
    }

    override suspend fun iterate(type: String): Flow<SentTelegramMessageType> {
        return iterate().filter { it.type == type }
    }

    override suspend fun add(id: Long, type: String): SentTelegramMessageType {
        val telegramMessageType = SerializedMessage(id, type)
        telegramMessages = telegramMessages + listOf(telegramMessageType)
        return telegramMessageType
    }

    override suspend fun delete(id: Long) {
        telegramMessages = telegramMessages.filter { it.getId() != id }
    }

    fun clear() {
        telegramMessages = emptyList()
    }

    fun assertThat(): Matcher {
        return Matcher()
    }

    inner class Matcher {

        fun presents(messageId: Long, type: String) {
            val sentMessageType = SerializedMessage(messageId, type)
            if(telegramMessages.contains(sentMessageType).not()){
                val errorBuilder = StringBuilder()
                    .appendLine("No Sent Message Type: $sentMessageType")
                    .appendLine("List: $telegramMessages")
                    .toString()
                error(errorBuilder)
            }
        }
    }
}