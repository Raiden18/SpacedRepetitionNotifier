package org.danceofvalkyries.notion.databases

import kotlinx.coroutines.flow.Flow

interface NotionDataBases {
    suspend fun iterate(): Flow<NotionDataBase>
    fun getBy(id: String): NotionDataBase
    suspend fun add(notionDataBase: NotionDataBase): NotionDataBase
    suspend fun clear()
}