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
            telegramButtons = emptyList(),
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    )

    init {

        beforeTest {
            clearAllMocks()
            editNotificationMessageUseCase = EditNotificationMessageUseCase(telegramChatRepository)
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(oldTelegramMessage)
        }

        test("Should update message") {
            val newMessageBody = TelegramMessageBody(
                text = "Something new",
                telegramButtons = emptyList(),
                telegramImageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )

            editNotificationMessageUseCase.execute(newMessageBody)

            coVerifyOrder {
                telegramChatRepository.updateInDb(newMessageBody, oldTelegramMessage.id)
                telegramChatRepository.editInChat(newMessageBody, oldTelegramMessage.id)
            }
        }
    }
}