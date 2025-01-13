package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetAllPagesFromNotionDataBase {
    suspend fun execute(notionId: NotionId): List<FlashCardNotionPage>
}