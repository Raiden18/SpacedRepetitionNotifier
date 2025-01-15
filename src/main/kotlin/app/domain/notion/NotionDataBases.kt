package org.danceofvalkyries.app.domain.notion

interface NotionDataBases {
    suspend fun iterate(): Sequence<NotionDataBase>
    suspend fun add(
        id: String,
        name: String,
    ): NotionDataBase

    suspend fun clear()
}