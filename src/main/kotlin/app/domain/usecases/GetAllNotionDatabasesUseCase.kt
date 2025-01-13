package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi

fun interface GetAllNotionDatabasesUseCase {
    suspend fun execute(): List<NotionDataBase>
}

fun GetAllNotionDatabasesUseCase(
    repository: NotionDataBaseApi,
): GetAllNotionDatabasesUseCase {
    return GetAllNotionDatabasesUseCase {
        repository.getFromCache()
    }
}