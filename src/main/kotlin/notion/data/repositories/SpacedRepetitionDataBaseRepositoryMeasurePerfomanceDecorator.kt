package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.utils.printMeasure

class SpacedRepetitionDataBaseRepositoryMeasurePerfomanceDecorator(
    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository
) : SpacedRepetitionDataBaseRepository {

    override suspend fun getAll(): SpacedRepetitionDataBaseGroup {
        return printMeasure(
            message = "getAll notion Databases",
        ) {
            spacedRepetitionDataBaseRepository.getAll()
        }
    }
}