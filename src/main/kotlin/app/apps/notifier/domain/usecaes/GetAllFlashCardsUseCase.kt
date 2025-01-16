package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCardNotionPage>
}

fun GetAllFlashCardsUseCase(
    notionDatabases: NotionDataBases,
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDatabases.iterate()
            .flatMap { it.iterate() }
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }.toList()
    }
}