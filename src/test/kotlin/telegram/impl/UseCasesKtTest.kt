package telegram.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramImageUrl
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat

class UseCasesKtTest : FunSpec() {

    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val textMessage = TelegramMessage(
        id = 1,
        body = TelegramMessageBody.EMPTY
            .copy(
                imageUrl = null,
            )
    )
    private val photoMessage = TelegramMessage(
        id = 2,
        body = TelegramMessageBody.EMPTY
            .copy(
                imageUrl = TelegramImageUrl("a url"),
            )
    )

    init {
        beforeTest {
            clearAllMocks()
            coEvery { telegramChatApi.sendTextMessage(textMessage.body) } returns textMessage
            coEvery { telegramChatApi.sendPhotoMessage(photoMessage.body) } returns photoMessage
        }

        context("Send Message") {
            lateinit var sendMessageToTelegramChat: SendMessageToTelegramChat
            beforeTest {
                sendMessageToTelegramChat = SendMessageToTelegramChat(telegramChatApi)
            }

            test("Should send textMessage if there is no image url") {
                sendMessageToTelegramChat.execute(textMessage.body) shouldBe textMessage
            }

            test("Should send photo message if there is image url") {
                sendMessageToTelegramChat.execute(photoMessage.body) shouldBe photoMessage
            }
        }
    }
}