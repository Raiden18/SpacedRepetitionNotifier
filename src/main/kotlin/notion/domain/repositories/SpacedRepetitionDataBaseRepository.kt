package org.danceofvalkyries.notion.domain.repositories

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

interface SpacedRepetitionDataBaseRepository {
    suspend fun getAll(): SpacedRepetitionDataBaseGroup
}