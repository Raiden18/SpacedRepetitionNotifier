package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
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