package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class DeleteOldAndSendNewNotificationUseCaseImplTest : FunSpec() {

    private val telegramChatRepository: TelegramChatRepository = mockk(relaxed = true)
    private val text = "Message to telegram"
    private val newTelegramMessage = TelegramMessage(
        id = 228,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList(),
            imageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )
    private val oldTelegramMessage = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList(),
            imageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )

    private lateinit var useCase: DeleteOldAndSendNewNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            useCase = DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository
            )
            coEvery { telegramChatRepository.sendToChat(newTelegramMessage.body) } returns newTelegramMessage
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(oldTelegramMessage)
        }

        test("Should replace old tg message with new one") {

            useCase.execute(newTelegramMessage.body)

            coVerify(exactly = 1) { telegramChatRepository.deleteFromDb(oldTelegramMessage) }
            coVerify(exactly = 1) { telegramChatRepository.deleteFromChat(oldTelegramMessage) }

            coVerify(exactly = 1) { telegramChatRepository.saveToDb(newTelegramMessage) }
            coVerify(exactly = 1) { telegramChatRepository.sendToChat(newTelegramMessage.body) }
        }
    }
}