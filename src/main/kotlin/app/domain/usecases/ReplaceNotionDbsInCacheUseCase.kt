package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.notion.api.GetDataBaseFromNotion
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceNotionDbsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceNotionDbsInCacheUseCase(
    dbIds: List<NotionId>,
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    getDataBaseFromNotion: GetDataBaseFromNotion,
    dispatchers: Dispatchers,
): ReplaceNotionDbsInCacheUseCase {
    return ReplaceNotionDbsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionDatabaseDataBaseTable.clear() }

            val fetchNotionDbsFromNotionAsync = dbIds.map { async(dispatchers.io) { getDataBaseFromNotion.execute(it) } }

            clearAsync.await()
            val notionDataBases = fetchNotionDbsFromNotionAsync.awaitAll()

            notionDatabaseDataBaseTable.insert(notionDataBases)
        }
    }
}