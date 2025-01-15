package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceNotionDbsInCacheUseCase
import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceNotionDbsInCacheUseCaseKtTest : FunSpec() {

    private val notionApi: NotionApi = mockk(relaxed = true)
    private val notionDatabases: NotionDataBases = mockk(relaxed = true)

    private val id = NotionId("1")
    private val dbIds = listOf(id)
    private lateinit var replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase

    init {
        beforeTest {
            replaceNotionDbsInCacheUseCase = ReplaceNotionDbsInCacheUseCase(
                dbIds,
                notionDatabases,
                notionApi,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newNotionDb = NotionDataBase.EMPTY.copy(
                id = NotionId("228"),
                name = "322"
            )
            coEvery { notionApi.getDataBase(id) } returns newNotionDb

            replaceNotionDbsInCacheUseCase.execute()

            coVerifyOrder {
                notionDatabases.clear()
                notionDatabases.add(
                    id = "228",
                    name = "322"
                )
            }
        }
    }
}