package org.danceofvalkyries.notion.data.repositories.api

import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse

interface NotionDataBaseApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageResponse>
}