package org.danceofvalkyries.notion.impl.flashcardpage

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

interface FlashCardNotionPageApi {
    suspend fun getFromNotion(notionId: NotionId): FlashCardNotionPage
    suspend fun updateInNotion(flashCardNotionPage: FlashCardNotionPage)
    suspend fun getAllFromDb(notionId: NotionId): List<FlashCardNotionPage>
}