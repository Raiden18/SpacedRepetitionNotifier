package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.api.mappers.toFlashCard
import org.danceofvalkyries.notion.data.repositories.db.flashcards.FlashCardDbTable
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.FlashCardsRepository

class FlashCardsRepositoryImpl(
    private val flashCardTable: FlashCardDbTable,
    private val notionApi: NotionApi,
) : FlashCardsRepository {

    override suspend fun getFromNotion(notionDbId: NotionDbId): List<FlashCard> {
        return notionApi.getContentFor(notionDbId.valueId)
            .map { it.toFlashCard() }
    }

    override suspend fun saveToDb(flashCards: List<FlashCard>) {
        flashCards.forEach { flashCardTable.insert(it) }
    }

    override suspend fun getFromDb(notionDbId: NotionDbId): List<FlashCard> {
        return flashCardTable.getAllFor(notionDbId.valueId)
    }

    override suspend fun clearDb() {
        flashCardTable.clear()
    }
}