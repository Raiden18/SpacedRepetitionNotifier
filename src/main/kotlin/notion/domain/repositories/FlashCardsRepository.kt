package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId

interface FlashCardsRepository {
    suspend fun getFromNotion(notionDbId: NotionDbId): List<FlashCard>

    suspend fun saveToDb(flashCards: List<FlashCard>)
    suspend fun getFromDb(notionDbId: NotionDbId): List<FlashCard>
    suspend fun clearDb()
}