package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCard>
}

fun GetAllFlashCardsUseCase(
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    flashCardsRepository: FlashCardsRepository,
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDatabaseDataBaseTable.getAll()
            .map { it.id }
            .flatMap { flashCardsRepository.getFromDbForTable(it) }
    }
}