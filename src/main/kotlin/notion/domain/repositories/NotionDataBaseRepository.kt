package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionId

interface NotionDataBaseRepository {
    suspend fun getFromNotion(id: NotionId): NotionDataBase

    @Deprecated("Move to app")
    suspend fun saveToCache(dbs: List<NotionDataBase>)

    @Deprecated("Move to app")
    suspend fun getFromCache(): List<NotionDataBase>

    @Deprecated("Move to app")
    suspend fun clearCache()
}