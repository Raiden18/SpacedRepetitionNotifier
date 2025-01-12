package telegram.data.repositories

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.danceofvalkyries.app.domain.models.ImageUrl
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class TelegramChatChatRepositoryImplTest : FunSpec() {

    private val api: TelegramChatApi = mockk(relaxed = true)
    private val textMessageResponse: TelegramMessage = mockk(relaxed = true)
    private val photoMessageResponse: TelegramMessage = mockk(relaxed = true)
    private val db: TelegramNotificationMessageDb = mockk(relaxed = true)

    private lateinit var telegramChatRepositoryImpl: TelegramChatRepositoryImpl

    init {
        beforeTest {
            clearAllMocks()
            telegramChatRepositoryImpl = TelegramChatRepositoryImpl(api, db)
            coEvery { api.sendMessage(any()) } returns textMessageResponse
            coEvery { api.sendPhoto(any()) } returns photoMessageResponse
        }

        test("Should send text message if there is no photo") {
            val textMessage = TelegramMessageBody(
                text = "Text message",
                nestedButtons = emptyList(),
                imageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )

            telegramChatRepositoryImpl.sendToChat(textMessage) shouldBe textMessageResponse
        }

        test("Should send photo if there is photo") {
            val textMessage = TelegramMessageBody(
                text = "Text message",
                nestedButtons = emptyList(),
                imageUrl = ImageUrl("photo url"),
                type = TelegramMessageBody.Type.NOTIFICATION,
            )

            telegramChatRepositoryImpl.sendToChat(textMessage) shouldBe photoMessageResponse
        }
    }
}