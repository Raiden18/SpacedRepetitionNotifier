package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.notion.domain.models.FlashCardNotionPage
import org.danceofvalkyries.notion.domain.models.NotionDataBase

interface NotionDataBaseRepository {
    suspend fun getFromNotion(id: Id): NotionDataBase

    @Deprecated("Move to app")
    suspend fun saveToCache(dbs: List<NotionDataBase>)

    @Deprecated("Move to app")
    suspend fun getFromCache(): List<NotionDataBase>

    @Deprecated("Move to app")
    suspend fun clearCache()
}