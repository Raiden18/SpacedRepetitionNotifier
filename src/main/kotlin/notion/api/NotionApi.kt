package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

interface NotionApi {
    suspend fun getDataBase(id: NotionId): NotionDataBase
    suspend fun getFlashCardPage(notionId: NotionId): FlashCardNotionPage
    suspend fun update(flashCardNotionPage: FlashCardNotionPage)
    suspend fun getFlashCardPagesFromDb(notionId: NotionId): List<FlashCardNotionPage>
}