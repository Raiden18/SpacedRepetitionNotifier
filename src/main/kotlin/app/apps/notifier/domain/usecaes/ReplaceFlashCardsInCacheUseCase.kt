package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import app.domain.notion.databases.NotionDataBases
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardsInCacheUseCase {
    suspend fun execute()
}

fun ReplaceFlashCardsInCacheUseCase(
    ids: List<NotionId>,
    notionDataBases: NotionDataBases,
    notionApi: NotionApi,
    dispatchers: Dispatchers
): ReplaceFlashCardsInCacheUseCase {
    return ReplaceFlashCardsInCacheUseCase {
        coroutineScope {
            val clearAsync = async(dispatchers.io) {
                notionDataBases.iterate().forEach {
                    it.clear()
                }
            }
            val fetchFromNotionAsync = ids.map { async(dispatchers.io) { notionApi.getFlashCardPagesFromDb(it) } }

            clearAsync.await()
            val flashCards = fetchFromNotionAsync.awaitAll().flatten()

            flashCards.forEach { flashCard ->
                val db = notionDataBases.iterate().first { it.id == flashCard.notionDbID.rawValue }
                db.add(
                    id = flashCard.id.rawValue,
                    coverUrl = flashCard.coverUrl,
                    name = flashCard.name,
                    explanation = flashCard.explanation,
                    example = flashCard.example,
                    knowLevels = flashCard.knowLevels.levels
                )
            }
        }
    }
}