package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.notion.domain.repositories.FlashCardsTablesRepository

fun interface GetFlashCardsTablesUseCase {
    suspend fun execute(): FlashCardsTablesGroup
}

fun GetFlashCardsTablesUseCase(
    flashCardsTablesRepository: FlashCardsTablesRepository
): GetFlashCardsTablesUseCase {
    return GetFlashCardsTablesUseCase {
        flashCardsTablesRepository.clear()
        val notionFlashCardsTables = flashCardsTablesRepository.getFromNotion()
        flashCardsTablesRepository.saveToDb(notionFlashCardsTables.allFlashCards)
        notionFlashCardsTables
    }
}
