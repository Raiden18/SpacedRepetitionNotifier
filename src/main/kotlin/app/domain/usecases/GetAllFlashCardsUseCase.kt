package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCard>
}

fun GetAllFlashCardsUseCase(
    notionDataBaseRepository: NotionDataBaseRepository,
    flashCardsRepository: FlashCardsRepository
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDataBaseRepository.getFromCache()
            .map { it.id }
            .flatMap { flashCardsRepository.getFromDbForTable(Id(it.rawValue)) }
    }
}