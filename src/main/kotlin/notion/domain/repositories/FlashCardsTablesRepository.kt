package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup

interface FlashCardsTablesRepository {
    suspend fun getFromNotion(): FlashCardsTablesGroup
    suspend fun saveToDb(flashCards: List<FlashCard>)
    suspend fun getFromDb(notionDbId: String): List<FlashCard>
    suspend fun clear()
}