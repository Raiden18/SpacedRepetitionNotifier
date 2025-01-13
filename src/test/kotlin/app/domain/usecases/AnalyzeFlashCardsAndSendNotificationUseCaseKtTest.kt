package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.usecases.*
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class AnalyzeFlashCardsAndSendNotificationUseCaseKtTest : FunSpec() {

    private val editNotificationMessageUseCase: EditNotificationMessageUseCase = mockk(relaxed = true)
    private val deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase = mockk(relaxed = true)
    private val getAllFlashCardsUseCase: GetAllFlashCardsUseCase = mockk(relaxed = true)
    private val notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable = mockk(relaxed = true)

    private val notificationMessage: TelegramMessageBody = mockk(relaxed = true)
    private val doneMessage: TelegramMessageBody = mockk(relaxed = true)

    private val messageFactory: MessageFactory = mockk(relaxed = true)

    private lateinit var analyzeFlashCardsAndSendNotificationUseCase: AnalyzeFlashCardsAndSendNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            every { messageFactory.createNotification(any(), any()) } returns notificationMessage
            every { messageFactory.createDone() } returns doneMessage
            analyzeFlashCardsAndSendNotificationUseCase = AnalyzeFlashCardsAndSendNotificationUseCase(
                getAllFlashCardsUseCase,
                notionDatabaseDataBaseTable,
                editNotificationMessageUseCase,
                deleteOldAndSendNewNotificationUseCase,
                messageFactory,
                threshold = 1,
            )
        }

        test("Should send notification message if there is flashcards more tan threshold") {
            coEvery { getAllFlashCardsUseCase.execute() } returns listOf(
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
            )

            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) { deleteOldAndSendNewNotificationUseCase.execute(notificationMessage) }
        }

        test("Should send done message if there is flashcards less than threshold") {
            coEvery { getAllFlashCardsUseCase.execute() } returns emptyList()

            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) { editNotificationMessageUseCase.execute(doneMessage) }
        }
    }
}