package org.danceofvalkyries.notion.data.repositories.api

import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse

interface NotionDataBaseApi {
    suspend fun getDescription(): NotionDbResponse
    suspend fun getContent(): List<NotionPageResponse>
}