package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceNotionDbsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceNotionDbsInCacheUseCase(
    dbIds: List<NotionId>,
    notionDataBaseApi: NotionDataBaseApi,
    dispatchers: Dispatchers,
): ReplaceNotionDbsInCacheUseCase {
    return ReplaceNotionDbsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionDataBaseApi.clearCache() }

            val fetchNotionDbsFromNotionAsync = dbIds.map { async(dispatchers.io) { notionDataBaseApi.getFromNotion(it) } }

            clearAsync.await()
            val notionDataBases = fetchNotionDbsFromNotionAsync.awaitAll()

            notionDataBaseApi.saveToCache(notionDataBases)
        }
    }
}