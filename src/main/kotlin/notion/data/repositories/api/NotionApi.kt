package org.danceofvalkyries.notion.data.repositories.api

import org.danceofvalkyries.notion.data.repositories.api.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.response.NotionPageResponse

interface NotionApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageResponse>
    suspend fun getNotionPage(id: String): NotionPageResponse
}