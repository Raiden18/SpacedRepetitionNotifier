package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

interface NotionApi {
    suspend fun update(flashCardNotionPage: FlashCardNotionPage)
}