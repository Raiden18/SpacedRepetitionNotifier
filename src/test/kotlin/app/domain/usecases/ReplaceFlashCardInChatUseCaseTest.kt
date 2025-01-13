package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardInChatUseCase
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.DeleteMessageFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.impl.TelegramChatApi
import org.danceofvalkyries.utils.DispatchersImpl
import testutils.MessageFactoryFake

class ReplaceFlashCardInChatUseCaseTest : FunSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val deleteMessageFromTelegramChat: DeleteMessageFromTelegramChat = mockk(relaxed = true)
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

    private val flashCard = FlashCardNotionPage.EMPTY.copy(
        id = NotionId("123"),
        notionDbID = NotionId("228")
    )

    private val anotherFlashCard =FlashCardNotionPage.EMPTY.copy(
        id = NotionId("333"),
        notionDbID = NotionId("228")
    )

    private lateinit var replaceFlashCardInChatUseCase: ReplaceFlashCardInChatUseCase

    init {
        beforeTest {
            clearAllMocks()
            messageFactoryFake = MessageFactoryFake()
            replaceFlashCardInChatUseCase = ReplaceFlashCardInChatUseCase(
                telegramMessagesDataBaseTable,
                deleteMessageFromTelegramChat,
                sendMessageToTelegramChat,
                messageFactoryFake,
                DispatchersImpl(kotlinx.coroutines.Dispatchers.Unconfined)
            )

            coEvery { sendMessageToTelegramChat.execute(flashCardMessage.body) } returns flashCardMessage

        }

        test("Should send FlashCard message to TG if there is no flashcard message in chat") {
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(notification)
            messageFactoryFake.flashCardBody = flashCardMessage.body

            replaceFlashCardInChatUseCase.execute(flashCard)

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(flashCardMessage.body) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.save(flashCardMessage) }
        }

        test("Should update FlashCard message if it already presents") {
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(flashCardMessage, notification)
            coEvery { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) } returns anotherFlashCardMessage
            messageFactoryFake.flashCardBody = anotherFlashCardMessage.body

            replaceFlashCardInChatUseCase.execute(anotherFlashCard)

            coVerify(exactly = 1) { deleteMessageFromTelegramChat.execute(flashCardMessage) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.delete(flashCardMessage) }

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.save(anotherFlashCardMessage) }
        }
    }
}