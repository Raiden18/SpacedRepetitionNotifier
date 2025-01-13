package org.danceofvalkyries.notion.impl.database

import notion.impl.client.NotionApi
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

class NotionDataBaseApiImpl(
    private val notionApi: NotionApi,
) : NotionDataBaseApi {

    override suspend fun getFromNotion(id: NotionId): NotionDataBase {
        val response = notionApi.getNotionDb(id.get())
        return NotionDataBase(
            id = NotionId(response.id),
            name = response.name,
        )
    }
}