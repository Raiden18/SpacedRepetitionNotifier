package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetPageFromNotion {
    suspend fun execute(notionId: NotionId): FlashCardNotionPage
}