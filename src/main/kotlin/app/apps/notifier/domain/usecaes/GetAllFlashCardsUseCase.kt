package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCardNotionPage>
}

fun GetAllFlashCardsUseCase(
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDatabaseDataBaseTable.getAll()
            .map { it.id }
            .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) }
    }
}