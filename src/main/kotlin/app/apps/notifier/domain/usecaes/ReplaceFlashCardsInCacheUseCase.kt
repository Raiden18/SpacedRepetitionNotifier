package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceFlashCardsInCacheUseCase(
    ids: List<NotionId>,
    notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
    notionApi: NotionApi,
    dispatchers: Dispatchers
): ReplaceFlashCardsInCacheUseCase {
    return ReplaceFlashCardsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionPageFlashCardDataBaseTable.clear() }
            val fetchFromNotionAsync = ids.map { async(dispatchers.io) { notionApi.getFlashCardPagesFromDb(it) } }

            clearAsync.await()
            val flashCards = fetchFromNotionAsync.awaitAll().flatten()

            notionPageFlashCardDataBaseTable.insert(flashCards)
        }
    }
}