package org.danceofvalkyries.app.data.restful.notion.databases

import app.domain.notion.databases.NotionDataBase
import app.domain.notion.databases.NotionDataBases
import com.google.gson.Gson
import okhttp3.OkHttpClient

class RestFulNotionDataBases(
    private val desiredDbIds: List<String>,
    private val apiKey: String,
    private val client: OkHttpClient,
    private val gson: Gson,
) : NotionDataBases {

    override suspend fun iterate(): Sequence<NotionDataBase> {
        return desiredDbIds.asSequence()
            .map {
                RestFulNotionDataBase(
                    id = it,
                    apiKey = apiKey,
                    client = client,
                    gson = gson,
                )
            }
    }

    override suspend fun add(notionDataBase: NotionDataBase) =
        error("Adding Dbs to Notion is not going to be supported")

    override suspend fun add(id: String, name: String): NotionDataBase =
        error("Adding Dbs to Notion is not going to be supported")

    override suspend fun clear() = error("Clearing Notion Db is not going to be supported")
}