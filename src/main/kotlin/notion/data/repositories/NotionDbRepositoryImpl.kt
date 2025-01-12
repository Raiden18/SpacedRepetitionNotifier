package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.db.table.NotionDataBaseDbTable
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository

class NotionDbRepositoryImpl(
    private val notionApi: NotionApi,
    private val notionDataBaseDbTable: NotionDataBaseDbTable,
) : NotionDbRepository {

    override suspend fun getFromNotion(id: Id): NotionDataBase {
        val response = notionApi.getNotionDb(id.valueId)
        return NotionDataBase(
            id = Id(response.id),
            name = response.name,
        )
    }

    override suspend fun saveToDb(tables: List<NotionDataBase>) {
        tables.forEach { notionDataBaseDbTable.insert(it) }
    }

    override suspend fun getFromDb(): List<NotionDataBase> {
        return notionDataBaseDbTable.getAll()
    }

    override suspend fun clearDb() {
        notionDataBaseDbTable.clear()
    }
}