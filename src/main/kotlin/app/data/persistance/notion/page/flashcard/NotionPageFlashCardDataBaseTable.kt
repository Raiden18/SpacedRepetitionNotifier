package org.danceofvalkyries.app.data.persistance.notion.page.flashcard

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

interface NotionPageFlashCardDataBaseTable {
    suspend fun insert(flashCardPage: FlashCardNotionPage)
    suspend fun insert(flashCardPages: List<FlashCardNotionPage>)
    suspend fun getAllFor(notionDataBaseId: NotionId): List<FlashCardNotionPage>
    suspend fun delete(flashCardPage: FlashCardNotionPage)
    suspend fun clear()
}