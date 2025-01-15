package org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao


interface NotionPageFlashCardDao {
    suspend fun insert(entity: NotionPageFlashCardDbEntity)
    suspend fun getAllFor(notionDataBaseId: String): List<NotionPageFlashCardDbEntity>
    suspend fun getBy(notionPageId: String): NotionPageFlashCardDbEntity?
    suspend fun delete(notionPageId: String)
    suspend fun clear()
}