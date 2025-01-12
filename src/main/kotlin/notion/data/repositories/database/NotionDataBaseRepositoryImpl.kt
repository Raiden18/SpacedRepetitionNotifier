package org.danceofvalkyries.notion.data.repositories.database

import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.database.db.NotionDataBaseDbTable
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository

class NotionDataBaseRepositoryImpl(
    private val notionApi: NotionApi,
    private val notionDataBaseDbTable: NotionDataBaseDbTable,
) : NotionDataBaseRepository {

    override suspend fun getFromNotion(id: Id): NotionDataBase {
        val response = notionApi.getNotionDb(id.valueId)
        return NotionDataBase(
            id = NotionId(response.id),
            name = response.name,
        )
    }

    override suspend fun saveToCache(dbs: List<NotionDataBase>) {
        dbs.forEach { notionDataBaseDbTable.insert(it) }
    }

    override suspend fun getFromCache(): List<NotionDataBase> {
        return notionDataBaseDbTable.getAll()
    }

    override suspend fun clearCache() {
        notionDataBaseDbTable.clear()
    }
}