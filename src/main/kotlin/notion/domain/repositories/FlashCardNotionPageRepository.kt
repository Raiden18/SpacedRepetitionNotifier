package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.FlashCardNotionPage

interface FlashCardNotionPageRepository {
    suspend fun getFromNotion(id: String): FlashCardNotionPage
    suspend fun updateInNotion(flashCardNotionPage: FlashCardNotionPage)
    suspend fun getAllFromDb(id: String): List<FlashCardNotionPage>
}