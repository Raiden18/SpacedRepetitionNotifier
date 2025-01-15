package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceNotionDbsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceNotionDbsInCacheUseCase(
    dbIds: List<NotionId>,
    notionDataBases: NotionDataBases,
    notionApi: NotionApi,
    dispatchers: Dispatchers,
): ReplaceNotionDbsInCacheUseCase {
    return ReplaceNotionDbsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionDataBases.clear() }

            val fetchNotionDbsFromNotionAsync = dbIds.map { async(dispatchers.io) { notionApi.getDataBase(it) } }

            clearAsync.await()
            val notionDataBasesRest = fetchNotionDbsFromNotionAsync.awaitAll()

            notionDataBasesRest.forEach {
                notionDataBases.add(
                    id = it.id.rawValue,
                    name = it.name
                )
            }
        }
    }
}