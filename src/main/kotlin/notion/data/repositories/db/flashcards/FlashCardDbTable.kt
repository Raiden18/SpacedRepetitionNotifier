package org.danceofvalkyries.notion.data.repositories.db.flashcards

import org.danceofvalkyries.notion.domain.models.FlashCard

interface FlashCardDbTable {
    suspend fun insert(flashCard: FlashCard)
    suspend fun getAllFor(notionDataBaseId: String): List<FlashCard>
    suspend fun delete(flashCard: FlashCard)
    suspend fun clear()
}