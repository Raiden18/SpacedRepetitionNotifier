package org.danceofvalkyries.notion.data.repositories.db.flashcards

interface FlashCardDbTable {
    suspend fun insert(entity: FlashCardDbEntity)
    suspend fun getAllFor(notionDataBaseId: String): List<FlashCardDbEntity>
    suspend fun delete(entity: FlashCardDbEntity)
    suspend fun clear()
}