package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceNotionDbsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceNotionDbsInCacheUseCase(
    dbIds: List<Id>,
    notionDbRepository: NotionDbRepository,
    dispatchers: Dispatchers,
): ReplaceNotionDbsInCacheUseCase {
    return ReplaceNotionDbsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionDbRepository.clearDb() }

            val fetchNotionDbsFromNotionAsync = dbIds.map { async(dispatchers.io) { notionDbRepository.getFromNotion(it) } }

            clearAsync.await()
            val notionDataBases = fetchNotionDbsFromNotionAsync.awaitAll()

            notionDbRepository.saveToDb(notionDataBases)
        }
    }
}