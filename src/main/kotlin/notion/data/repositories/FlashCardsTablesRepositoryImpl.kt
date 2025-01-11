package org.danceofvalkyries.notion.data.repositories

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.mappers.toFlashCard
import org.danceofvalkyries.notion.data.repositories.db.FlashCardDbTable
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.FlashCardTable
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.notion.domain.repositories.FlashCardsTablesRepository
import org.danceofvalkyries.utils.Dispatchers
import kotlin.time.Duration

class FlashCardsTablesRepositoryImpl(
    private val delayBetweenRequests: Duration,
    private val apis: List<NotionDataBaseApi>,
    private val dispatchers: Dispatchers,
    private val flashCardTable: FlashCardDbTable,
) : FlashCardsTablesRepository {

    override suspend fun getFromNotion(): FlashCardsTablesGroup {
        return coroutineScope {
            val descriptionsTasks = apis.map { async(dispatchers.io) { it.getDescription() } }
            val contentsTasks = apis.map { async(dispatchers.io) { it.getContent().map { it.toFlashCard() } } }

            val description = descriptionsTasks.awaitAll()
            val contents = contentsTasks
                .awaitAll()

            val list = description.mapIndexed { index, notionDbResponse ->
                val content = contents[index]
                notionDbResponse to content
            }.map {
                FlashCardTable(
                    id = it.first.id,
                    name = it.first.name,
                    flashCards = it.second
                )
            }

            FlashCardsTablesGroup(list)
        }
    }

    override suspend fun saveToDb(flashCards: List<FlashCard>) {
        flashCards.forEach { flashCard ->
            flashCardTable.insert(flashCard)
        }
    }

    override suspend fun getFromDb(notionDbId: String): List<FlashCard> {
        return flashCardTable.getAllFor(notionDbId)
    }

    override suspend fun clear() {
        flashCardTable.clear()
    }
}