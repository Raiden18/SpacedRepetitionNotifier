package org.danceofvalkyries.app.data.restful.notion.databases

import app.domain.notion.databases.NotionDataBase
import app.domain.notion.databases.NotionDataBases
import com.google.gson.Gson
import okhttp3.OkHttpClient

class RestNotionDataBases(
    private val apiKey: String,
    private val client: OkHttpClient,
    private val gson: Gson,
) : NotionDataBases {

    private val desiredIds = mutableListOf<NotionDataBase>()

    override suspend fun iterate(): Sequence<NotionDataBase> {
        return desiredIds.asSequence()
    }

    override suspend fun add(
        id: String,
        name: String
    ): NotionDataBase {
        return RestNotionDataBase(
            id = id,
            apiKey = apiKey,
            client = client,
            gson = gson,
        ).also { desiredIds.add(it) }
    }

    override suspend fun clear() = error("Clearing Notion Db is not going to be supported")
}