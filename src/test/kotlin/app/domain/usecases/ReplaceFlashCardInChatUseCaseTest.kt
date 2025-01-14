package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardInChatUseCase
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.DeleteMessageFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceFlashCardInChatUseCaseTest : FunSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val deleteMessageFromTelegramChat: DeleteMessageFromTelegramChat = mockk(relaxed = true)
    private val getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard = mockk(relaxed = true)
    private val sendMessageToTelegramChat: SendMessageToTelegramChat = mockk(relaxed = true)

    private val notification = TelegramMessage(
        id = 1,
        body = TelegramMessageBody.EMPTY
    )

    private val flashCardMessage = TelegramMessage(
        id = 2,
        body = TelegramMessageBody.EMPTY
    )
    private val anotherFlashCardMessage = TelegramMessage(
        id = 3,
        body = TelegramMessageBody.EMPTY
    )

    private val flashCard = FlashCardNotionPage.EMPTY.copy(
        id = NotionId("123"),
        notionDbID = NotionId("228")
    )

    private val anotherFlashCard = FlashCardNotionPage.EMPTY.copy(
        id = NotionId("333"),
        notionDbID = NotionId("228")
    )

    private lateinit var replaceFlashCardInChatUseCase: ReplaceFlashCardInChatUseCase

    init {
        beforeTest {
            clearAllMocks()
            replaceFlashCardInChatUseCase = ReplaceFlashCardInChatUseCase(
                telegramMessagesDataBaseTable,
                deleteMessageFromTelegramChat,
                sendMessageToTelegramChat,
                getOnlineDictionariesForFlashCard,
                DispatchersImpl(kotlinx.coroutines.Dispatchers.Unconfined)
            )

            coEvery { sendMessageToTelegramChat.execute(flashCardMessage.body) } returns flashCardMessage
            coEvery { getOnlineDictionariesForFlashCard.execute(any()) } returns emptyList()
        }

        test("Should send FlashCard message to TG if there is no flashcard message in chat") {
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(notification)

            replaceFlashCardInChatUseCase.execute(flashCard)

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(flashCardMessage.body) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.save(flashCardMessage, TODO()) }
        }

        test("Should update FlashCard message if it already presents") {
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(flashCardMessage, notification)
            coEvery { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) } returns anotherFlashCardMessage

            replaceFlashCardInChatUseCase.execute(anotherFlashCard)

            coVerify(exactly = 1) { deleteMessageFromTelegramChat.execute(flashCardMessage) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.delete(flashCardMessage) }

            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(anotherFlashCardMessage.body) }
            coVerify(exactly = 1) { telegramMessagesDataBaseTable.save(anotherFlashCardMessage, TODO()) }
        }
    }
}