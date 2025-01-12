package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCard>
}

fun GetAllFlashCardsUseCase(
    notionDbRepository: NotionDbRepository,
    flashCardsRepository: FlashCardsRepository
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDbRepository.getFromDb()
            .map { it.id }
            .flatMap { flashCardsRepository.getFromDb(it) }
    }
}