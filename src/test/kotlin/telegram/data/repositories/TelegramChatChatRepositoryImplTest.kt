package telegram.data.repositories

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.repositories.TelegramChatChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class TelegramChatChatRepositoryImplTest : FunSpec() {

    private val api: TelegramChatApi = mockk(relaxed = true)
    private val textMessageResponse: TelegramMessage = mockk(relaxed = true)
    private val photoMessageResponse: TelegramMessage = mockk(relaxed = true)

    private lateinit var telegramChatChatRepositoryImpl: TelegramChatChatRepositoryImpl

    init {
        beforeTest {
            clearAllMocks()
            telegramChatChatRepositoryImpl = TelegramChatChatRepositoryImpl(api)
            coEvery { api.sendMessage(any()) } returns textMessageResponse
            coEvery { api.sendPhoto(any()) } returns photoMessageResponse
        }

        test("Should send text message if there is no photo") {
            val textMessage = TelegramMessageBody(
                text = "Text message",
                nestedButtons = emptyList(),
                photoUrl = null,
            )

            telegramChatChatRepositoryImpl.sendToChat(textMessage) shouldBe textMessageResponse
        }

        test("Should send photo if there is photo") {
            val textMessage = TelegramMessageBody(
                text = "Text message",
                nestedButtons = emptyList(),
                photoUrl = "photo url",
            )

            telegramChatChatRepositoryImpl.sendToChat(textMessage) shouldBe photoMessageResponse
        }
    }
}