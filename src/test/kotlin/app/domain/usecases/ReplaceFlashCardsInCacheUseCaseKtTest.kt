package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.notion.api.GetAllPagesFromNotionDataBase
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceFlashCardsInCacheUseCaseKtTest : FunSpec() {

    private val notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable = mockk(relaxed = true)
    private val getAllPagesFromNotionDataBase: GetAllPagesFromNotionDataBase = mockk(relaxed = true)
    private val id = NotionId("1")
    private val dbIds = listOf(id)
    private lateinit var replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase

    init {
        beforeTest {
            replaceFlashCardsInCacheUseCase = ReplaceFlashCardsInCacheUseCase(
                dbIds,
                notionPageFlashCardDataBaseTable,
                getAllPagesFromNotionDataBase,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newFlashCard = FlashCardNotionPage.EMPTY
            coEvery { getAllPagesFromNotionDataBase.execute(id) } returns listOf(newFlashCard)

            replaceFlashCardsInCacheUseCase.execute()

            coVerifyOrder {
                notionPageFlashCardDataBaseTable.clear()
                notionPageFlashCardDataBaseTable.insert(listOf(newFlashCard))
            }
        }
    }
}