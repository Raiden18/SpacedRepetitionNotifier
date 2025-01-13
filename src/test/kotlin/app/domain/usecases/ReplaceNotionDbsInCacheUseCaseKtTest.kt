package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.domain.usecases.ReplaceNotionDbsInCacheUseCase
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceNotionDbsInCacheUseCaseKtTest : FunSpec() {

    private val motionDbRepository: NotionDataBaseRepository = mockk(relaxed = true)
    private val id = NotionId("1")
    private val dbIds = listOf(id)
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
            coEvery { motionDbRepository.getFromNotion(id) } returns newNotionDb

            replaceNotionDbsInCacheUseCase.execute()

            coVerifyOrder {
                motionDbRepository.clearCache()
                motionDbRepository.saveToCache(listOf(newNotionDb))
            }
        }
    }
}