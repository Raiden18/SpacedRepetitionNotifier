package org.danceofvalkyries.app.domain.repositories

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id

interface FlashCardsRepository {
    suspend fun getFromNotion(id: Id): List<FlashCard>

    suspend fun saveToDb(flashCards: List<FlashCard>)
    suspend fun getFromDbForTable(id: Id): List<FlashCard>
    suspend fun clearDb()
}