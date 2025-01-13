package org.danceofvalkyries.app.data.repositories.flashcards

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.FlashCardDbTable
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.OnlineDictionary
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.config.domain.Config
import notion.impl.client.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId

class FlashCardsRepositoryImpl(
    private val flashCardTable: FlashCardDbTable,
    private val notionApi: NotionApi,
    private val config: Config,
) : FlashCardsRepository {

    override suspend fun getFromNotion(id: NotionId): List<FlashCard> {
        return notionApi.getContentFor(id.get(NotionId.Modifier.AS_IS))
            .map { it.toFlashCard() }
            .map { it.copy(onlineDictionaries = getOnlineDictionariesFor(id)) }
    }

    override suspend fun saveToDb(flashCards: List<FlashCard>) {
        flashCards.map { it.toEntity() }
            .forEach { flashCardTable.insert(it) }
    }

    override suspend fun getFromDbForTable(id: NotionId): List<FlashCard> {
        return flashCardTable.getAllFor(id.get(NotionId.Modifier.AS_IS))
            .map { it.toFlashCard(getOnlineDictionariesFor(id)) }
    }

    override suspend fun clearDb() {
        flashCardTable.clear()
    }

    private fun getOnlineDictionariesFor(id: NotionId): List<OnlineDictionary> {
        return config.notion
            .observedDatabases
            .firstOrNull { id == NotionId(it.id) }
            ?.dictionaries
            ?.map { OnlineDictionary(it) } ?: emptyList()
    }
}