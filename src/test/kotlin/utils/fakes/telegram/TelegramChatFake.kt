package utils.fakes.telegram

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage

class TelegramChatFake : TelegramChat {

    private var sentTelegramMessages = listOf<TelegramMessageFake>()

    private var ID = 0L

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

    inner class Matcher {

        fun wasSent(telegramMessage: TelegramMessage) {
            sentTelegramMessages shouldContain telegramMessage
        }

        fun wasDeleted(telegramMessage: TelegramMessage) {
            sentTelegramMessages shouldNotContain telegramMessage
        }

        fun textMessageWasEdited(from: TelegramMessage, to: TelegramMessage) {
            from.id shouldBe to.id
            from.text shouldNotBe to.text
        }
    }

    override suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}