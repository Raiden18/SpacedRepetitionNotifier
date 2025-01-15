package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.domain.notion.NotionDataBases
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCardNotionPage>
}

fun GetAllFlashCardsUseCase(
    notionDatabases: NotionDataBases,
    notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDatabases.iterate()
            .map { NotionId(it.id) }
            .toList()
            .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) }
    }
}