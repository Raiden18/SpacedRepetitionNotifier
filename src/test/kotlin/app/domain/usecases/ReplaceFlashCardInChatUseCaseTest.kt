package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardInChatUseCase
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody
import org.danceofvalkyries.utils.DispatchersImpl
import testutils.MessageFactoryFake

class ReplaceFlashCardInChatUseCaseTest : FunSpec() {

    private val telegramChatRepository: TelegramChatRepository = mockk(relaxed = true)

    private lateinit var messageFactoryFake: MessageFactoryFake

    private val notification = TelegramMessage(
        id = 1,
        body = TelegramMessageBody.EMPTY.copy(
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )

    private val flashCardMessage = TelegramMessage(
        id = 2,
        body = TelegramMessageBody.EMPTY.copy(
            type = TelegramMessageBody.Type.FLASH_CARD
        )
    )
    private val anotherFlashCardMessage = TelegramMessage(
        id = 3,
        body = TelegramMessageBody.EMPTY.copy(
            type = TelegramMessageBody.Type.FLASH_CARD
        )
    )

    private val flashCard = FlashCard.EMPTY.copy(
        metaInfo = FlashCard.MetaInfo(
            id = "123",
            notionDbId = "228"
        )
    )

    private val anotherFlashCard = FlashCard.EMPTY.copy(
        metaInfo = FlashCard.MetaInfo(
            id = "333",
            notionDbId = "228"
        )
    )

    private lateinit var replaceFlashCardInChatUseCase: ReplaceFlashCardInChatUseCase

    init {
        beforeTest {
            clearAllMocks()
            messageFactoryFake = MessageFactoryFake()
            replaceFlashCardInChatUseCase = ReplaceFlashCardInChatUseCase(
                telegramChatRepository,
                messageFactoryFake,
                DispatchersImpl(kotlinx.coroutines.Dispatchers.Unconfined)
            )

            coEvery { telegramChatRepository.sendToChat(flashCardMessage.body) } returns flashCardMessage

        }

        test("Should send FlashCard message to TG if there is no flashcard message in chat") {
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(notification)
            messageFactoryFake.flashCardBody = flashCardMessage.body

            replaceFlashCardInChatUseCase.execute(flashCard)

            coVerify(exactly = 1) { telegramChatRepository.sendToChat(flashCardMessage.body) }
            coVerify(exactly = 1) { telegramChatRepository.saveToDb(flashCardMessage) }
        }

        test("Should update FlashCard message if it already presents") {
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(flashCardMessage, notification)
            coEvery { telegramChatRepository.sendToChat(anotherFlashCardMessage.body) } returns anotherFlashCardMessage
            messageFactoryFake.flashCardBody = anotherFlashCardMessage.body

            replaceFlashCardInChatUseCase.execute(anotherFlashCard)

            coVerify(exactly = 1) { telegramChatRepository.deleteFromChat(flashCardMessage) }
            coVerify(exactly = 1) { telegramChatRepository.deleteFromDb(flashCardMessage) }

            coVerify(exactly = 1) { telegramChatRepository.sendToChat(anotherFlashCardMessage.body) }
            coVerify(exactly = 1) { telegramChatRepository.saveToDb(anotherFlashCardMessage) }
        }
    }
}