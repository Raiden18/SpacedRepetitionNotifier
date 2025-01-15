package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCards
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceFlashCardsInCacheUseCaseKtTest : FunSpec() {

    private val notionPageFlashCards: NotionPageFlashCards = mockk(relaxed = true)
    private val notionApi: NotionApi = mockk(relaxed = true)
    private val id = NotionId("1")
    private val dbIds = listOf(id)
    private lateinit var replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase

    init {
        beforeTest {
            replaceFlashCardsInCacheUseCase = ReplaceFlashCardsInCacheUseCase(
                dbIds,
                notionPageFlashCards,
                notionApi,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newFlashCard = FlashCardNotionPage.EMPTY
            coEvery { notionApi.getFlashCardPagesFromDb(id) } returns listOf(newFlashCard)

            replaceFlashCardsInCacheUseCase.execute()

            coVerifyOrder {
                notionPageFlashCards.clear()
                notionPageFlashCards.add(
                    id = newFlashCard.id.rawValue,
                    coverUrl = newFlashCard.coverUrl,
                    notionDbId = newFlashCard.notionDbID.rawValue,
                    name = newFlashCard.name,
                    explanation = newFlashCard.explanation,
                    example = newFlashCard.example,
                    knowLevels = newFlashCard.knowLevels.levels
                )
            }
        }
    }
}