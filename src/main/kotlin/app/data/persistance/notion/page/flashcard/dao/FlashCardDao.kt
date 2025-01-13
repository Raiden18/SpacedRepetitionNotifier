package org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao

interface FlashCardDao {
    suspend fun insert(entity: FlashCardDbEntity)
    suspend fun getAllFor(notionDataBaseId: String): List<FlashCardDbEntity>
    suspend fun delete(entity: FlashCardDbEntity)
    suspend fun clear()
}