package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.EditNotificationMessageUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.GetAllFlashCardsUseCase
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import utils.NotionDataBaseFake

class AnalyzeFlashCardsAndSendNotificationUseCaseKtTest : FunSpec() {

    private val editNotificationMessageUseCase: EditNotificationMessageUseCase = mockk(relaxed = true)
    private val deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase = mockk(relaxed = true)
    private val getAllFlashCardsUseCase: GetAllFlashCardsUseCase = mockk(relaxed = true)
    private val notionDatabases: NotionDataBases = mockk(relaxed = true)

    private lateinit var analyzeFlashCardsAndSendNotificationUseCase: AnalyzeFlashCardsAndSendNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            analyzeFlashCardsAndSendNotificationUseCase = AnalyzeFlashCardsAndSendNotificationUseCase(
                getAllFlashCardsUseCase,
                notionDatabases,
                editNotificationMessageUseCase,
                deleteOldAndSendNewNotificationUseCase,
                threshold = 1,
            )
        }

        test("Should send notification message if there is flashcards more tan threshold") {
            val flashCards = listOf<FlashCardNotionPage>(
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
            )
            val dataBases = sequenceOf(
                NotionDataBaseFake(
                    id = "228",
                    name = "322",
                )
            )
            coEvery { getAllFlashCardsUseCase.execute() } returns flashCards
            coEvery { notionDatabases.iterate() } returns dataBases

            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) {
                deleteOldAndSendNewNotificationUseCase.execute(
                    NeedRevisingNotificationMessage(
                        flashCards,
                        listOf(NotionDataBase(NotionId("228"), "322"))
                    )
                )
            }
        }

        test("Should send done message if there is flashcards less than threshold") {
            coEvery { getAllFlashCardsUseCase.execute() } returns emptyList()

            analyzeFlashCardsAndSendNotificationUseCase.execute()

            coVerify(exactly = 1) { editNotificationMessageUseCase.execute(DoneMessage()) }
        }
    }
}