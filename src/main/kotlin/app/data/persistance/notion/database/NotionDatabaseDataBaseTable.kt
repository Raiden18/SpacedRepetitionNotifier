package org.danceofvalkyries.app.data.persistance.notion.database

import org.danceofvalkyries.notion.api.models.NotionDataBase

interface NotionDatabaseDataBaseTable {
    suspend fun insert(notionDataBases: List<NotionDataBase>)
    suspend fun getAll(): List<NotionDataBase>
    suspend fun clear()
}