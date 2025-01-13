package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.notion.api.models.NotionDataBase

fun interface GetAllNotionDatabasesUseCase {
    suspend fun execute(): List<NotionDataBase>
}

fun GetAllNotionDatabasesUseCase(
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable
): GetAllNotionDatabasesUseCase {
    return GetAllNotionDatabasesUseCase { notionDatabaseDataBaseTable.getAll() }
}