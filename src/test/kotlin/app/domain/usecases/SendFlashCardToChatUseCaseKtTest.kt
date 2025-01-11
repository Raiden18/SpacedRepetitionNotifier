package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.domain.usecases.SendFlashCardToChatUseCase
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody
import testutils.MessageFactoryFake

class SendFlashCardToChatUseCaseKtTest : FunSpec() {

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
            notionDbId = NotionDbId("228")
        )
    )

    private val anotherFlashCard = FlashCard.EMPTY.copy(
        metaInfo = FlashCard.MetaInfo(
            id = "333",
            notionDbId = NotionDbId("228")
        )
    )

    private lateinit var sendFlashCardToChatUseCase: SendFlashCardToChatUseCase

    init {
        beforeTest {
            clearAllMocks()
            messageFactoryFake = MessageFactoryFake()
            sendFlashCardToChatUseCase = SendFlashCardToChatUseCase(
                telegramChatRepository,
                messageFactoryFake,
            )

            coEvery { telegramChatRepository.sendToChat(flashCardMessage.body) } returns flashCardMessage
            //coEvery { telegramChatRepository.sendToChat(anotherFlashCardMessage.body) } returns anotherFlashCardMessage
        }

        test("Should send FlashCard message to TG if there is no flashcard message in chat") {
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(notification)
            messageFactoryFake.flashCardBody = flashCardMessage.body

            sendFlashCardToChatUseCase.execute(flashCard)

            coVerify(exactly = 1) { telegramChatRepository.sendToChat(flashCardMessage.body) }
            coVerify(exactly = 1) { telegramChatRepository.saveToDb(flashCardMessage) }
        }

        test("Should update FlashCard message if it already presents") {
            coEvery { telegramChatRepository.getAllFromDb() } returns listOf(flashCardMessage, notification)
            messageFactoryFake.flashCardBody = anotherFlashCardMessage.body

            sendFlashCardToChatUseCase.execute(anotherFlashCard)

            coVerify(exactly = 1) {
                telegramChatRepository.editInChat(
                    anotherFlashCardMessage.body,
                    flashCardMessage.id
                )
            }
            coVerify(exactly = 1) {
                telegramChatRepository.updateInDb(
                    anotherFlashCardMessage.body,
                    flashCardMessage.id
                )
            }
        }
    }
}