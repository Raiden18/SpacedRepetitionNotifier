package org.danceofvalkyries.app.data.persistance.notion.database

interface NotionDataBaseDao {
    suspend fun insert(notionDataBaseEntity: NotionDataBaseEntity)
    suspend fun getAll(): List<NotionDataBaseEntity>
    suspend fun clear()
}