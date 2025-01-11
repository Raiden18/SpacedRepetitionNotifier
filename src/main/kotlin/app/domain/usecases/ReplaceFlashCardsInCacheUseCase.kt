package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceFlashCardsInCacheUseCase(
    notionDbIds: List<NotionDbId>,
    flashCardsRepository: FlashCardsRepository,
    dispatchers: Dispatchers
): ReplaceFlashCardsInCacheUseCase {
    return ReplaceFlashCardsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { flashCardsRepository.clearDb() }
            val fetchFromNotionAsync = notionDbIds.map { async(dispatchers.io) { flashCardsRepository.getFromNotion(it) } }

            clearAsync.await()
            val flashCards = fetchFromNotionAsync.awaitAll().flatten()

            flashCardsRepository.saveToDb(flashCards)
        }
    }
}