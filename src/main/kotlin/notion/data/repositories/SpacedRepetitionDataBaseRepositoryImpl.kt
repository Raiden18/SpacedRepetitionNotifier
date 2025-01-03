package org.danceofvalkyries.notion.data.repositories

import kotlinx.coroutines.delay
import org.danceofvalkyries.notion.api.NotionDataBaseApi
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import kotlin.time.Duration

class SpacedRepetitionDataBaseRepositoryImpl(
    private val delayBetweenRequests: Duration,
    private val apis: List<NotionDataBaseApi>
) : SpacedRepetitionDataBaseRepository {

    override suspend fun getAll(): SpacedRepetitionDataBaseGroup {
        val list = apis.map {
            delay(delayBetweenRequests)
            it.getDescription() to it.getContent()
        }.map {
            val dbResponse = it.first
            val flashCardsResponse = it.second
            SpacedRepetitionDataBase(
                id = dbResponse.id,
                name = dbResponse.name,
                flashCards = flashCardsResponse.map { FlashCard }
            )
        }
        return SpacedRepetitionDataBaseGroup(list)
    }
}