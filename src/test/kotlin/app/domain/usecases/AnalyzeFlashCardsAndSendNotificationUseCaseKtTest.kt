package app.domain.usecases

import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.EditNotificationMessageUseCase
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import utils.NotionDataBaseFake
import utils.NotionDataBasesFake
import utils.NotionPageFlashCardFake

class AnalyzeFlashCardsAndSendNotificationUseCaseKtTest : FunSpec() {

    private val editNotificationMessageUseCase: EditNotificationMessageUseCase = mockk(relaxed = true)
    private val deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase = mockk(relaxed = true)
    private val notionDatabases: NotionDataBases = mockk(relaxed = true)

    private lateinit var analyzeFlashCardsAndSendNotificationUseCase: AnalyzeFlashCardsAndSendNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
        }

        test("Should send notification message if there is flashcards more tan threshold") {
            val flashCards = listOf<NotionPageFlashCard>(
                NotionPageFlashCardFake("1"),
                NotionPageFlashCardFake("2"),
                NotionPageFlashCardFake("3")
            )
            val dataBase = NotionDataBaseFake(
                id = "228",
                name = "322",
                pages = flashCards
            )
            AnalyzeFlashCardsAndSendNotificationUseCase(
                NotionDataBasesFake(listOf(dataBase)),
                editNotificationMessageUseCase,
                deleteOldAndSendNewNotificationUseCase,
                threshold = 1,
            ).execute()

            coVerify(exactly = 1) {
                deleteOldAndSendNewNotificationUseCase.execute(
                    NeedRevisingNotificationMessage(
                        NotionDataBasesFake(listOf(dataBase))
                    )
                )
            }
        }

        test("Should send done message if there is flashcards less than threshold") {
            val dataBase = NotionDataBaseFake(
                id = "228",
                name = "322",
                pages = emptyList()
            )

            AnalyzeFlashCardsAndSendNotificationUseCase(
                NotionDataBasesFake(listOf(dataBase)),
                editNotificationMessageUseCase,
                deleteOldAndSendNewNotificationUseCase,
                threshold = 1,
            ).execute()

            coVerify(exactly = 1) { editNotificationMessageUseCase.execute(DoneMessage()) }
        }
    }
}