package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.domain.usecases.ReplaceNotionDbsInCacheUseCase
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceNotionDbsInCacheUseCaseKtTest : FunSpec() {

    private val motionDbRepository: NotionDbRepository = mockk(relaxed = true)
    private val notionDbId = NotionDbId("1")
    private val dbIds = listOf(notionDbId)
    private lateinit var replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase

    init {
        beforeTest {
            replaceNotionDbsInCacheUseCase = ReplaceNotionDbsInCacheUseCase(
                dbIds,
                motionDbRepository,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newNotionDb = NotionDataBase.EMPTY
            coEvery { motionDbRepository.getFromNotion(notionDbId) } returns newNotionDb

            replaceNotionDbsInCacheUseCase.execute()

            coVerifyOrder {
                motionDbRepository.clearDb()
                motionDbRepository.saveToDb(listOf(newNotionDb))
            }
        }
    }
}