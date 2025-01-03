package org.danceofvalkyries.notion.api

import notion.api.rest.response.FlashCardResponse
import notion.api.rest.response.NotionDbResponse

interface NotionDataBaseApi {
    suspend fun getDescription(): NotionDbResponse
    suspend fun getContent(): List<FlashCardResponse>
}