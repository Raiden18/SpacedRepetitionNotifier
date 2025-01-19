package utils.fakes.telegram

import io.kotest.matchers.collections.shouldNotContain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import org.danceofvalkyries.telegram.chat.TelegramChat
import org.danceofvalkyries.telegram.message.TelegramMessage

class TelegramChatFake : TelegramChat {

    var sentTelegramMessages = listOf<TelegramMessageFake>()
    private val userCallback = MutableStateFlow<TelegramMessage.Button.Callback?>(null)

    var ID = 0L

    override suspend fun sendTextMessage(
        text: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val message = TelegramMessageFake(
            id = ++ID,
            text = text,
            imageUrl = null,
            nestedButtons = nestedButtons
        )
        sentTelegramMessages = sentTelegramMessages + listOf(message)
        return message
    }

    fun assertThat(): Matcher {
        return Matcher()
    }

    fun userSendsCallback(callback: TelegramMessage.Button.Callback) {
        userCallback.value = callback
    }

    override suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val message = TelegramMessageFake(
            id = ++ID,
            text = caption,
            imageUrl = imageUrl,
            nestedButtons = nestedButtons
        )
        sentTelegramMessages = sentTelegramMessages + listOf(message)
        return message
    }

    override suspend fun delete(messageId: Long) {
        sentTelegramMessages = sentTelegramMessages.filter { it.id != messageId }
    }

    override suspend fun edit(
        messageId: Long,
        newText: String,
        newNestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        sentTelegramMessages = sentTelegramMessages.map {
            if (it.id == messageId) {
                it.copy(text = newText, nestedButtons = newNestedButtons)
            } else {
                it
            }
        }.toMutableList()
        return getMessage(messageId)
    }

    override suspend fun getMessage(messageId: Long): TelegramMessage {
        return sentTelegramMessages.first { it.id == messageId }
    }

    override fun getEvents(): Flow<TelegramMessage.Button.Callback> {
        return userCallback.mapNotNull { it }
    }

    inner class Matcher {

        fun isInChat(telegramMessage: TelegramMessage) {
            if (sentTelegramMessages.contains(telegramMessage).not()) {
                val stringBuilder = StringBuilder()
                    .appendLine("Does not contain: $telegramMessage")
                    .appendLine("List: $sentTelegramMessages")
                    .toString()
                error(stringBuilder)
            }
        }

        fun wasDeleted(telegramMessage: TelegramMessage) {
            sentTelegramMessages shouldNotContain telegramMessage
        }
    }
}