package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceNotionDbsInCacheUseCase
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceNotionDbsInCacheUseCaseKtTest : FunSpec() {

    private val notionApi: NotionApi = mockk(relaxed = true)
    private val notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable = mockk(relaxed = true)

    private val id = NotionId("1")
    private val dbIds = listOf(id)
    private lateinit var replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase

    init {
        beforeTest {
            replaceNotionDbsInCacheUseCase = ReplaceNotionDbsInCacheUseCase(
                dbIds,
                notionDatabaseDataBaseTable,
                notionApi,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should clear flash cards in db, fetch them from notion and save them") {
            val newNotionDb = NotionDataBase.EMPTY
            coEvery { notionApi.getDataBase(id) } returns newNotionDb

            replaceNotionDbsInCacheUseCase.execute()

            coVerifyOrder {
                notionDatabaseDataBaseTable.clear()
                notionDatabaseDataBaseTable.insert(listOf(newNotionDb))
            }
        }
    }
}