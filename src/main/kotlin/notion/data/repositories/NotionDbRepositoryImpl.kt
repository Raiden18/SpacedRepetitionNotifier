package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.db.table.FlashCardsTablesDbTable
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository

class NotionDbRepositoryImpl(
    private val notionApi: NotionApi,
    private val flashCardsTablesDbTable: FlashCardsTablesDbTable,
) : NotionDbRepository {

    override suspend fun getFromNotion(): FlashCardsTablesGroup {
        TODO()
        /* return coroutineScope {
             val descriptionsTasks = apis.map { async(dispatchers.io) { it.getNotionDb() } }
             val contentsTasks = apis.map { async(dispatchers.io) { it.getContentFor().map { it.toFlashCard() } } }

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
         }*/
    }

    override suspend fun getFromNotion(notionDbId: NotionDbId): NotionDataBase {
        val response = notionApi.getNotionDb(notionDbId.valueId)
        return NotionDataBase(
            id = NotionDbId(response.id),
            name = response.name,
        )
    }

    override suspend fun saveToDb(tables: List<NotionDataBase>) {
        tables.forEach { flashCardsTablesDbTable.insert(it) }
    }

    override suspend fun getFromDb(): List<NotionDataBase> {
        return flashCardsTablesDbTable.getAll()
    }

    override suspend fun clearDb() {
        flashCardsTablesDbTable.clear()
    }
}