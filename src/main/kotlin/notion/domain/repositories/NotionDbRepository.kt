package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionDbId

interface NotionDbRepository {
    suspend fun getFromNotion(notionDbId: NotionDbId): NotionDataBase
    suspend fun saveToDb(tables: List<NotionDataBase>)
    suspend fun getFromDb(): List<NotionDataBase>
    suspend fun clearDb()
}