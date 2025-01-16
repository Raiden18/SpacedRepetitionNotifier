package app.domain.usecases

import app.domain.notion.databases.NotionDataBase
import app.domain.notion.databases.NotionDataBases
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceFlashCardsInCacheUseCaseKtTest : FunSpec() {

    private val notionDb: NotionDataBase = mockk(relaxed = true)
    private val notionDatabases: NotionDataBases = mockk(relaxed = true)
    private val notionApi: NotionApi = mockk(relaxed = true)
    private val id = NotionId("1")
    private val dbIds = listOf(id)
    private lateinit var replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase

    init {
        beforeTest {
            replaceFlashCardsInCacheUseCase = ReplaceFlashCardsInCacheUseCase(
                dbIds,
                notionDatabases,
                notionApi,
                DispatchersImpl(Dispatchers.Unconfined)
            )

            coEvery { notionDatabases.iterate() } returns sequenceOf(notionDb)
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newFlashCard = FlashCardNotionPage.EMPTY
            coEvery { notionApi.getFlashCardPagesFromDb(id) } returns listOf(newFlashCard)

            replaceFlashCardsInCacheUseCase.execute()

            coVerifyOrder {
                notionDb.clear()
                notionDb.add(
                    id = newFlashCard.id.rawValue,
                    coverUrl = newFlashCard.coverUrl,
                    name = newFlashCard.name,
                    explanation = newFlashCard.explanation,
                    example = newFlashCard.example,
                    knowLevels = newFlashCard.knowLevels.levels
                )
            }
        }
    }
}