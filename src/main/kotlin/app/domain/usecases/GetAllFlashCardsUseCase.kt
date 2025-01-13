package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi

fun interface GetAllFlashCardsUseCase {
    suspend fun execute(): List<FlashCard>
}

fun GetAllFlashCardsUseCase(
    notionDataBaseApi: NotionDataBaseApi,
    flashCardsRepository: FlashCardsRepository
): GetAllFlashCardsUseCase {
    return GetAllFlashCardsUseCase {
        notionDataBaseApi.getFromCache()
            .map { it.id }
            .flatMap { flashCardsRepository.getFromDbForTable(it) }
    }
}