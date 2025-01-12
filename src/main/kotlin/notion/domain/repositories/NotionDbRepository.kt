package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.app.domain.models.Id

interface NotionDbRepository {
    suspend fun getFromNotion(id: Id): NotionDataBase
    suspend fun saveToDb(tables: List<NotionDataBase>)
    suspend fun getFromDb(): List<NotionDataBase>
    suspend fun clearDb()
}