package org.danceofvalkyries.notion.data.repositories.api

import org.danceofvalkyries.notion.data.repositories.api.models.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.models.NotionPageData

interface NotionApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageData>
    suspend fun getNotionPage(id: String): NotionPageData
    suspend fun updateInNotion(notionPageData: NotionPageData)
}