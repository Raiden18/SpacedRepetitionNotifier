package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceFlashCardsInCacheUseCaseKtTest : FunSpec() {

    private val flashCardsRepository: FlashCardsRepository = mockk(relaxed = true)
    private val notionDbId = NotionDbId("1")
    private val dbIds = listOf(notionDbId)
    private lateinit var replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase

    init {
        beforeTest {
            replaceFlashCardsInCacheUseCase = ReplaceFlashCardsInCacheUseCase(
                dbIds,
                flashCardsRepository,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newFlashCard = FlashCard.EMPTY
            coEvery { flashCardsRepository.getFromNotion(notionDbId) } returns listOf(newFlashCard)

            replaceFlashCardsInCacheUseCase.execute()

            coVerifyOrder {
                flashCardsRepository.clearDb()
                flashCardsRepository.saveToDb(listOf(newFlashCard))
            }
        }
    }
}