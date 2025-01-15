package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCards
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCardNotionPage>
}

fun GetAllFlashCardsUseCase(
    notionDatabases: NotionDataBases,
    notionPageFlashCards: NotionPageFlashCards,
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDatabases.iterate()
            .map { NotionId(it.id) }
            .toList()
            .flatMap { notionDb ->
                notionPageFlashCards.iterate().filter { it.notionDbID == notionDb.rawValue }
            }.map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )

            }
    }
}