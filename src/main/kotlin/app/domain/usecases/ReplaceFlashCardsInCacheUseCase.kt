package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceFlashCardsInCacheUseCase(
    ids: List<NotionId>,
    flashCardsRepository: FlashCardsRepository,
    dispatchers: Dispatchers
): ReplaceFlashCardsInCacheUseCase {
    return ReplaceFlashCardsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { flashCardsRepository.clearDb() }
            val fetchFromNotionAsync = ids.map { async(dispatchers.io) { flashCardsRepository.getFromNotion(it) } }

            clearAsync.await()
            val flashCards = fetchFromNotionAsync.awaitAll().flatten()

            flashCardsRepository.saveToDb(flashCards)
        }
    }
}