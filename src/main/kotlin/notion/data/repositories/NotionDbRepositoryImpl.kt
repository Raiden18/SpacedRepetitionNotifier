package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.db.table.FlashCardsTablesDbTable
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository

class NotionDbRepositoryImpl(
    private val notionApi: NotionApi,
    private val flashCardsTablesDbTable: FlashCardsTablesDbTable,
) : NotionDbRepository {

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