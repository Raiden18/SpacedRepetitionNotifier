package org.danceofvalkyries.notion.impl.database

import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

interface NotionDataBaseApi {
    suspend fun getFromNotion(id: NotionId): NotionDataBase

    @Deprecated("Move to app")
    suspend fun saveToCache(dbs: List<NotionDataBase>)

    @Deprecated("Move to app")
    suspend fun getFromCache(): List<NotionDataBase>

    @Deprecated("Move to app")
    suspend fun clearCache()
}