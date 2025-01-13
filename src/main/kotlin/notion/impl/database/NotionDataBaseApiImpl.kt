package org.danceofvalkyries.notion.impl.database

import notion.impl.client.NotionApi
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDataBaseDao
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDataBaseEntity
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

class NotionDataBaseApiImpl(
    private val notionApi: NotionApi,
    private val notionDataBaseDao: NotionDataBaseDao,
) : NotionDataBaseApi {

    override suspend fun getFromNotion(id: NotionId): NotionDataBase {
        val response = notionApi.getNotionDb(id.get(NotionId.Modifier.URL_FRIENDLY))
        return NotionDataBase(
            id = NotionId(response.id),
            name = response.name,
        )
    }

    override suspend fun saveToCache(dbs: List<NotionDataBase>) {
        dbs.map {
            NotionDataBaseEntity(
                id = it.id.get(NotionId.Modifier.AS_IS),
                name = it.name
            )
        }.forEach { notionDataBaseDao.insert(it) }
    }

    override suspend fun getFromCache(): List<NotionDataBase> {
        return notionDataBaseDao.getAll()
            .map {
                NotionDataBase(
                    id = NotionId(it.id),
                    name = it.name,
                )
            }
    }

    override suspend fun clearCache() {
        notionDataBaseDao.clear()
    }
}