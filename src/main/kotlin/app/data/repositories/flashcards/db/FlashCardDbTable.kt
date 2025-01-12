package org.danceofvalkyries.app.data.repositories.flashcards.db

interface FlashCardDbTable {
    suspend fun insert(entity: FlashCardDbEntity)
    suspend fun getAllFor(notionDataBaseId: String): List<FlashCardDbEntity>
    suspend fun delete(entity: FlashCardDbEntity)
    suspend fun clear()
}