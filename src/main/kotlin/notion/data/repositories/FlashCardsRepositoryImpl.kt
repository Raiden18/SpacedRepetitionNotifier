package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.db.flashcards.FlashCardDbTable
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.models.OnlineDictionary
import org.danceofvalkyries.notion.domain.repositories.FlashCardsRepository

class FlashCardsRepositoryImpl(
    private val flashCardTable: FlashCardDbTable,
    private val notionApi: NotionApi,
    private val config: Config,
) : FlashCardsRepository {

    override suspend fun getFromNotion(notionDbId: NotionDbId): List<FlashCard> {
        return notionApi.getContentFor(notionDbId.valueId)
            .map { it.toFlashCard() }
            .map { it.copy(onlineDictionaries = getOnlineDictionariesFor(notionDbId)) }
    }

    override suspend fun saveToDb(flashCards: List<FlashCard>) {
        flashCards
            .map { it.toEntity() }
            .forEach { flashCardTable.insert(it) }
    }

    override suspend fun getFromDb(notionDbId: NotionDbId): List<FlashCard> {
        return flashCardTable.getAllFor(notionDbId.valueId)
            .map { it.toFlashCard(getOnlineDictionariesFor(notionDbId)) }
    }

    override suspend fun clearDb() {
        flashCardTable.clear()
    }

    private fun getOnlineDictionariesFor(notionDbId: NotionDbId): List<OnlineDictionary> {
        return config.notion
            .observedDatabases
            .firstOrNull { notionDbId.valueId == it.id }
            ?.dictionaries
            ?.map { OnlineDictionary(it) } ?: emptyList()
    }
}