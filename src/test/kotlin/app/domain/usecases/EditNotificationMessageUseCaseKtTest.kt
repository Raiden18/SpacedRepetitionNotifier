package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.EditNotificationMessageUseCase
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.TelegramChatApi
import utils.TelegramMessageFake

class EditNotificationMessageUseCaseKtTest : FunSpec() {

    private val telegramMessages: TelegramMessages = mockk(relaxed = true)
    private val telegramApi: TelegramChatApi = mockk(relaxed = true)
    private lateinit var editNotificationMessageUseCase: EditNotificationMessageUseCase

    private val oldTelegramMessage = TelegramMessageFake(
        id = 322,
        type = "type",
    )

    init {

        beforeTest {
            clearAllMocks()
            editNotificationMessageUseCase = EditNotificationMessageUseCase(telegramMessages, telegramApi)
            coEvery { telegramMessages.iterate() } returns sequenceOf(oldTelegramMessage)
        }

        test("Should update message") {
            val newMessage = DoneMessage()

            editNotificationMessageUseCase.execute(newMessage)

            coVerifyOrder {
                telegramApi.editInChat(newMessage.asTelegramBody(), oldTelegramMessage.id)
            }
        }
    }
}