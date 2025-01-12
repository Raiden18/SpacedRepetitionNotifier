package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository

fun interface GetAllNotionDatabasesUseCase {
    suspend fun execute(): List<NotionDataBase>
}

fun GetAllNotionDatabasesUseCase(
    repository: NotionDataBaseRepository,
): GetAllNotionDatabasesUseCase {
    return GetAllNotionDatabasesUseCase {
        repository.getFromCache()
    }
}