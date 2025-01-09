package org.danceofvalkyries.notion.data.repositories

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.notion.api.NotionDataBaseApi
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.utils.Dispatchers
import kotlin.time.Duration

class SpacedRepetitionDataBaseRepositoryImpl(
    private val delayBetweenRequests: Duration,
    private val apis: List<NotionDataBaseApi>,
    private val dispatchers: Dispatchers,
) : SpacedRepetitionDataBaseRepository {

    override suspend fun getAll(): SpacedRepetitionDataBaseGroup {
        return coroutineScope {
            val descriptionsTasks = apis.map { async(dispatchers.io) { it.getDescription() } }
            val contentsTasks = apis.map { async(dispatchers.io) { it.getContent() } }

            val description = descriptionsTasks.awaitAll()
            val contents = contentsTasks.awaitAll()

            val list = description.mapIndexed { index, notionDbResponse ->
                val content = contents[index]
                notionDbResponse to content
            }.map {
                SpacedRepetitionDataBase(
                    id = it.first.id,
                    name = it.first.name,
                    flashCards = it.second.map { FlashCard }
                )
            }
            SpacedRepetitionDataBaseGroup(list)
        }
    }
}