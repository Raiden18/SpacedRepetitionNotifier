package org.danceofvalkyries.app.data.notion.databases.restful

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.utils.HttpClient

class RestFulNotionDataBases(
    private val desiredDbIds: List<String>,
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) : NotionDataBases {

    override suspend fun iterate(): Sequence<NotionDataBase> {
        return desiredDbIds.asSequence()
            .map { getBy(it) }
    }

    override fun getBy(id: String): NotionDataBase {
        return RestFulNotionDataBase(
            id = id,
            apiKey = apiKey,
            httpClient = httpClient,
            okHttpClient = okHttpClient,
            gson = gson,
        )
    }

    override suspend fun add(notionDataBase: NotionDataBase) =
        error("Adding Dbs to Notion is not going to be supported")

    override suspend fun add(id: String, name: String): NotionDataBase =
        error("Adding Dbs to Notion is not going to be supported")

    override suspend fun clear() = error("Clearing Notion Db is not going to be supported")
}