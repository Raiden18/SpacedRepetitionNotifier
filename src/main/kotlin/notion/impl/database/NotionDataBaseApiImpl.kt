package org.danceofvalkyries.notion.impl.database

import org.danceofvalkyries.notion.impl.restapi.NotionApi
import org.danceofvalkyries.app.data.repositories.notion.db.NotionDataBaseDbTable
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

class NotionDataBaseApiImpl(
    private val notionApi: NotionApi,
    private val notionDataBaseDbTable: NotionDataBaseDbTable,
) : NotionDataBaseApi {

    override suspend fun getFromNotion(id: NotionId): NotionDataBase {
        val response = notionApi.getNotionDb(id.get(NotionId.Modifier.URL_FRIENDLY))
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