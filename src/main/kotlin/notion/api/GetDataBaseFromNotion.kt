package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

fun interface GetDataBaseFromNotion {
    suspend fun execute(notionId: NotionId): NotionDataBase
}