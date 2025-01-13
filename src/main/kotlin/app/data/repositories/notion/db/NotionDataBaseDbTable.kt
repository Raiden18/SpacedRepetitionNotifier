package org.danceofvalkyries.app.data.repositories.notion.db

import org.danceofvalkyries.notion.api.models.NotionDataBase

interface NotionDataBaseDbTable {
    suspend fun insert(notionDataBase: NotionDataBase)
    suspend fun getAll(): List<NotionDataBase>
    suspend fun clear()
}