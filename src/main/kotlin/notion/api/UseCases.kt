package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetDataBaseFromNotion {
    suspend fun execute(notionId: NotionId): NotionDataBase
}

fun interface GetPageFromNotion {
    suspend fun execute(notionId: NotionId): FlashCardNotionPage
}

fun interface GetAllPagesFromNotionDataBase {
    suspend fun execute(notionId: NotionId): List<FlashCardNotionPage>
}

fun interface UpdatePageInNotion {
    suspend fun execute(flashCardNotionPage: FlashCardNotionPage)
}