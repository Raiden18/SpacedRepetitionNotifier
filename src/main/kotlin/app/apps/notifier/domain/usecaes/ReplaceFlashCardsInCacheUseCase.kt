package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCards
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceFlashCardsInCacheUseCase(
    ids: List<NotionId>,
    notionPageFlashCards: NotionPageFlashCards,
    notionApi: NotionApi,
    dispatchers: Dispatchers
): ReplaceFlashCardsInCacheUseCase {
    return ReplaceFlashCardsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) { notionPageFlashCards.clear() }
            val fetchFromNotionAsync = ids.map { async(dispatchers.io) { notionApi.getFlashCardPagesFromDb(it) } }

            clearAsync.await()
            val flashCards = fetchFromNotionAsync.awaitAll().flatten()

            flashCards.forEach {
                notionPageFlashCards.add(
                    id = it.id.rawValue,
                    coverUrl = it.coverUrl,
                    notionDbId = it.notionDbID.rawValue,
                    name = it.name,
                    explanation = it.explanation,
                    example = it.example,
                    knowLevels = it.knowLevels.levels
                )
            }
        }
    }
}