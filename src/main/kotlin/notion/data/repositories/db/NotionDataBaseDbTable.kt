package org.danceofvalkyries.notion.data.repositories.db

import org.danceofvalkyries.notion.domain.models.NotionDataBase

interface NotionDataBaseDbTable {
    suspend fun insert(notionDataBase: NotionDataBase)
    suspend fun getAll(): List<NotionDataBase>
    suspend fun clear()
}