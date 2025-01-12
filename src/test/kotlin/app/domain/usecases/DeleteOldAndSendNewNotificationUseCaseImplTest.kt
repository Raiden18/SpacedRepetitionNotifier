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

    private val telegramNotification = TelegramMessage(
        id = 228,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList(),
            imageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )
    private val oldTelegramNotification = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList(),
            imageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )
    private val flashCardMessage = TelegramMessage(
        id = 1,
        body = TelegramMessageBody.EMPTY.copy(
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    )

    private lateinit var useCase: DeleteOldAndSendNewNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            useCase = DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository
            )
            coEvery { telegramChatRepository.sendToChat(telegramNotification.body) } returns telegramNotification
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(oldTelegramNotification, flashCardMessage)
        }

        test("Should replace old tg message with new one") {

            useCase.execute(telegramNotification.body)

            coVerify(exactly = 1) { telegramChatRepository.deleteFromDb(oldTelegramNotification) }
            coVerify(exactly = 1) { telegramChatRepository.deleteFromChat(oldTelegramNotification) }

            coVerify(exactly = 1) { telegramChatRepository.saveToDb(telegramNotification) }
            coVerify(exactly = 1) { telegramChatRepository.sendToChat(telegramNotification.body) }

            coVerify(exactly = 0) { telegramChatRepository.deleteFromDb(flashCardMessage) }
            coVerify(exactly = 0) { telegramChatRepository.deleteFromChat(flashCardMessage) }
        }
    }
}