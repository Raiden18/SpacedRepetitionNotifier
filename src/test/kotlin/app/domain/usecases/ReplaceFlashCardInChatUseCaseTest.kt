package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardInChatUseCase
import org.danceofvalkyries.telegram.api.DeleteFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.utils.DispatchersImpl
import testutils.MessageFactoryFake

class ReplaceFlashCardInChatUseCaseTest : FunSpec() {

    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val deleteFromTelegramChat: DeleteFromTelegramChat = mockk(relaxed = true)
    private val sendMessageToTelegramChat: SendMessageToTelegramChat = mockk(relaxed = true)

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
                telegramChatApi,
                deleteFromTelegramChat,
                sendMessageToTelegramChat,
                messageFactoryFake,
                DispatchersImpl(kotlinx.coroutines.Dispatchers.Unconfined)
            )

            coEvery { sendMessageToTelegramChat.execute(flashCardMessage.body) } returns flashCardMessage

        }

        test("Should send FlashCard message to TG if there is no flashcard message in chat") {
            coEvery { telegramChatApi.getAllFromDb() } returns listOf(notification)
            messageFactoryFake.flashCardBody = flashCardMessage.body

            replaceFlashCardInChatUseCase.execute(flashCard)

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(flashCardMessage.body) }
            coVerify(exactly = 1) { telegramChatApi.saveToDb(flashCardMessage) }
        }

        test("Should update FlashCard message if it already presents") {
            coEvery { telegramChatApi.getAllFromDb() } returns listOf(flashCardMessage, notification)
            coEvery { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) } returns anotherFlashCardMessage
            messageFactoryFake.flashCardBody = anotherFlashCardMessage.body

            replaceFlashCardInChatUseCase.execute(anotherFlashCard)

            coVerify(exactly = 1) { deleteFromTelegramChat.execute(flashCardMessage) }
            coVerify(exactly = 1) { telegramChatApi.deleteFromDb(flashCardMessage) }

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) }
            coVerify(exactly = 1) { telegramChatApi.saveToDb(anotherFlashCardMessage) }
        }
    }
}