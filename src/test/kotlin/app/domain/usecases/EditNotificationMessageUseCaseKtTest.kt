package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class EditNotificationMessageUseCaseKtTest : FunSpec() {

    private val telegramChatRepository: TelegramChatRepository = mockk(relaxed = true)
    private lateinit var editNotificationMessageUseCase: EditNotificationMessageUseCase

    private val oldTelegramMessage = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = "old",
            buttons = emptyList()
        )
    )

    init {

        beforeTest {
            clearAllMocks()
            editNotificationMessageUseCase = EditNotificationMessageUseCase(telegramChatRepository)
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(oldTelegramMessage)
        }

        test("Should update message") {
            val newMessage = "Something new"

            editNotificationMessageUseCase.execute(
                TelegramMessageBody(
                    text = newMessage,
                    buttons = emptyList()
                )
            )

            coVerifyOrder {
                telegramChatRepository.updateInDb(newMessage, oldTelegramMessage.id)
                telegramChatRepository.editInChat(newMessage, oldTelegramMessage.id)
            }
        }
    }
}