package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository

fun interface GetAllNotionDatabasesUseCase {
    suspend fun execute(): List<NotionDataBase>
}

fun GetAllNotionDatabasesUseCase(
    repository: NotionDbRepository,
): GetAllNotionDatabasesUseCase {
    return GetAllNotionDatabasesUseCase {
        repository.getFromDb()
    }
}