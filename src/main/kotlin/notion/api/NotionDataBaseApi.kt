package org.danceofvalkyries.notion.api

import notion.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.api.rest.response.NotionPageResponse

interface NotionDataBaseApi {
    suspend fun getDescription(): NotionDbResponse
    suspend fun getContent(): List<NotionPageResponse>
}