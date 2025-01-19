package org.danceofvalkyries.notion.databases

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.notion.pages.NotionPageFlashCard

interface NotionDataBase {
    fun getId(): String
    fun getName(): String
    fun iterate(): Flow<NotionPageFlashCard>
    fun clear()
    fun delete(pageId: String)
    suspend fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard
    suspend fun getPageBy(pageId: String): NotionPageFlashCard
}