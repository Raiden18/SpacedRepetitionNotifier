package utils

import io.kotest.matchers.collections.shouldContain
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessageType
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType

class SentTelegramMessagesTypeFake(
    private var telegramMessages: List<SentTelegramMessageType> = emptyList()
) : SentTelegramMessagesType {

    override suspend fun iterate(): Sequence<SentTelegramMessageType> {
        return telegramMessages.asSequence()
    }

    override suspend fun add(id: Long, type: String): SentTelegramMessageType {
        val telegramMessageType = SentTelegramMessageTypeFake(id, type)
        telegramMessages = telegramMessages + listOf(telegramMessageType)
        return telegramMessageType
    }

    override suspend fun delete(id: Long) {
        telegramMessages = telegramMessages.filter { it.id != id }
    }

    fun clear() {
        telegramMessages = emptyList()
    }

    fun assertThat(): Matcher {
        return Matcher()
    }

    inner class Matcher {

        fun presents(messageId: Long, type: String) {
            val sentMessageType = SentTelegramMessageTypeFake(messageId, type)
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