package org.danceofvalkyries.app.domain.repositories

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionId

interface FlashCardsRepository {
    suspend fun getFromNotion(id: NotionId): List<FlashCard>

    suspend fun saveToDb(flashCards: List<FlashCard>)
    suspend fun getFromDbForTable(id: NotionId): List<FlashCard>
    suspend fun clearDb()
}