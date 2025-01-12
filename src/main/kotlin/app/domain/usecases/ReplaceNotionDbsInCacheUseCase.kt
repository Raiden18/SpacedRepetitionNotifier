package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceNotionDbsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceNotionDbsInCacheUseCase(
    dbIds: List<Id>,
    notionDataBaseRepository: NotionDataBaseRepository,
    dispatchers: Dispatchers,
): ReplaceNotionDbsInCacheUseCase {
    return ReplaceNotionDbsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionDataBaseRepository.clearCache() }

            val fetchNotionDbsFromNotionAsync = dbIds.map {
                async(dispatchers.io) {
                    notionDataBaseRepository.getFromNotion(
                        NotionId(it.valueId)
                    )
                }
            }

            clearAsync.await()
            val notionDataBases = fetchNotionDbsFromNotionAsync.awaitAll()

            notionDataBaseRepository.saveToCache(notionDataBases)
        }
    }
}