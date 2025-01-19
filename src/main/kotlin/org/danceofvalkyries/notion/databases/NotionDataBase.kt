package org.danceofvalkyries.notion.databases

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.notion.pages.NotionPageFlashCard

interface NotionDataBase {
    fun getId(): String
    suspend fun clear()
    suspend fun delete(pageId: String)
    suspend fun getName(): String
    suspend fun iterate(): Flow<NotionPageFlashCard>
    suspend fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard
    suspend fun getPageBy(pageId: String): NotionPageFlashCard
}