package org.danceofvalkyries.job.data.notion.databases

interface NotionDataBases {
    suspend fun iterate(): Sequence<NotionDataBase>
    fun getBy(id: String): NotionDataBase
    suspend fun add(notionDataBase: NotionDataBase): NotionDataBase
    suspend fun clear()
}