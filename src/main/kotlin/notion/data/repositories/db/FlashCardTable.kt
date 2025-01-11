package org.danceofvalkyries.notion.data.repositories.db

import org.danceofvalkyries.notion.domain.models.FlashCard

interface FlashCardTable {
    suspend fun insert(flashCard: FlashCard)
    suspend fun getAllFor(notionDataBaseId: String): List<FlashCard>
}