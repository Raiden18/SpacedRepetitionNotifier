package org.danceofvalkyries.job

import com.google.gson.Gson
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.rest.clients.http.HttpClientImpl

fun UpdateCacheJob(
    dispatchers: Dispatchers,
    environment: Environment,
): Job {
    val dbConnection = environment.dataBase.establishConnection()
    val httpClient = HttpClientImpl(environment.httpClient)
    val restfulNotionDatabases = RestFulNotionDataBases(
        desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
        apiKey = environment.config.notion.apiKey,
        httpClient = httpClient,
        gson = Gson()
    )
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    return JobResourcesLifeCycleDecorator(
        dispatchers,
        httpClient,
        UpdateCacheJob(
            restfulNotionDatabases,
            sqlLiteNotionDatabases,
        )
    )
}

class UpdateCacheJob(
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteNotionDataBases: NotionDataBases,
) : Job {


    override val type: String = "update_cache"

    override suspend fun run() {
        clearAllCache()
        downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase()
    }

    private suspend fun downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase() {
        restfulNotionDataBases.iterate().collect { restfulNotionDb ->
            sqlLiteNotionDataBases.add(restfulNotionDb)
        }
    }

    private suspend fun clearAllCache() {
        sqlLiteNotionDataBases.clear()
    }
}