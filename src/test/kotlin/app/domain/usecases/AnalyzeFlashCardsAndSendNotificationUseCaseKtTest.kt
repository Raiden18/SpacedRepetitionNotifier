package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.usecases.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.app.domain.usecases.GetFlashCardsTablesUseCase
import org.danceofvalkyries.notion.domain.models.FlashCardTable
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class AnalyzeFlashCardsAndSendNotificationUseCaseKtTest : FunSpec() {

    private val getFlashCardsTablesUseCase: GetFlashCardsTablesUseCase = mockk(relaxed = true)
    private val editNotificationMessageUseCase: EditNotificationMessageUseCase = mockk(relaxed = true)
    private val deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase = mockk(relaxed = true)

    private val notificationMessage: TelegramMessageBody = mockk(relaxed = true)
    private val doneMessage: TelegramMessageBody = mockk(relaxed = true)

    private val messageFactory: MessageFactory = mockk(relaxed = true)

    private lateinit var analyzeFlashCardsAndSendNotificationUseCase: AnalyzeFlashCardsAndSendNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            every { messageFactory.createNotification(any()) } returns notificationMessage
            every { messageFactory.createDone() } returns doneMessage
            analyzeFlashCardsAndSendNotificationUseCase = AnalyzeFlashCardsAndSendNotificationUseCase(
                getFlashCardsTablesUseCase,
                editNotificationMessageUseCase,
                deleteOldAndSendNewNotificationUseCase,
                messageFactory,
                threshold = 1,
            )
        }

        test("Should send notification message if there is flashcards more tan threshold") {
            coEvery { getFlashCardsTablesUseCase.execute() } returns FlashCardsTablesGroup(
                listOf(
                    FlashCardTable(
                        id = "",
                        name = "",
                        flashCards = listOf(
                            mockk(),
                            mockk(),
                            mockk()
                        )
                    )
                )
            )
            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) { deleteOldAndSendNewNotificationUseCase.execute(notificationMessage) }
        }

        test("Should send done message if there is flashcards less than threshold") {
            coEvery { getFlashCardsTablesUseCase.execute() } returns FlashCardsTablesGroup(
                listOf(
                    FlashCardTable(
                        id = "",
                        name = "",
                        flashCards = emptyList()
                    )
                )
            )
            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) { editNotificationMessageUseCase.execute(doneMessage) }
        }
    }
}