package org.danceofvalkyries.notion.databases.restful

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.databases.NotionDataBases
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class RestFulNotionDataBases(
    private val desiredDbIds: List<String>,
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val gson: Gson,
) : NotionDataBases {

    override suspend fun iterate(): Flow<NotionDataBase> {
        return desiredDbIds.asFlow()
            .map { getBy(it) }
    }

    override fun getBy(id: String): NotionDataBase {
        return RestFulNotionDataBase(
            id = id,
            apiKey = apiKey,
            httpClient = httpClient,
            gson = gson,
        )
    }

    override suspend fun add(notionDataBase: NotionDataBase) = error("Adding Dbs to Notion is not going to be supported")
    override suspend fun clear() = error("Clearing Notion Db is not going to be supported")
}