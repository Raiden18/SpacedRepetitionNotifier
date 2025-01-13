package org.danceofvalkyries.notion.impl.restapi

import org.danceofvalkyries.notion.impl.restapi.models.response.NotionDbResponse
import org.danceofvalkyries.notion.impl.restapi.models.NotionPageData

interface NotionApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageData>
    suspend fun getNotionPage(id: String): NotionPageData
    suspend fun updateInNotion(notionPageData: NotionPageData)
}