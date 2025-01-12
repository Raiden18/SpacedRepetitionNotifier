package org.danceofvalkyries.app.data.repositories.flashcards

import org.danceofvalkyries.app.data.repositories.flashcards.db.FlashCardDbTable
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.app.domain.models.OnlineDictionary
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.data.repositories.api.NotionApi

class FlashCardsRepositoryImpl(
    private val flashCardTable: FlashCardDbTable,
    private val notionApi: NotionApi,
    private val config: Config,
) : FlashCardsRepository {

    override suspend fun getFromNotion(id: Id): List<FlashCard> {
        return notionApi.getContentFor(id.valueId)
            .map { it.toFlashCard() }
            .map { it.copy(onlineDictionaries = getOnlineDictionariesFor(id)) }
    }

    override suspend fun saveToDb(flashCards: List<FlashCard>) {
        flashCards
            .map { it.toEntity() }
            .forEach { flashCardTable.insert(it) }
    }

    override suspend fun getFromDb(id: Id): List<FlashCard> {
        return flashCardTable.getAllFor(id.valueId)
            .map { it.toFlashCard(getOnlineDictionariesFor(id)) }
    }

    override suspend fun clearDb() {
        flashCardTable.clear()
    }

    private fun getOnlineDictionariesFor(id: Id): List<OnlineDictionary> {
        return config.notion
            .observedDatabases
            .firstOrNull { id.valueId == it.id }
            ?.dictionaries
            ?.map { OnlineDictionary(it) } ?: emptyList()
    }
}