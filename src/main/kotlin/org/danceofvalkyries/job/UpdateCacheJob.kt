package org.danceofvalkyries.job

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import org.danceofvalkyries.utils.rest.clients.http.HttpClientImpl

fun UpdateCacheJob(
    dispatchers: Dispatchers,
    environment: Environment,
): UpdateCacheJob {
    val dbConnection = environment.dataBase.establishConnection()
    val httpClient = HttpClientImpl(environment.httpClient)
    val restfulNotionDatabases = RestFulNotionDataBases(
        desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
        apiKey = environment.config.notion.apiKey,
        httpClient = httpClient,
        gson = Gson()
    )
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    return UpdateCacheJob(
        dispatchers,
        restfulNotionDatabases,
        sqlLiteNotionDatabases,
        environment.dataBase,
    )
}

class UpdateCacheJob(
    private val dispatchers: Dispatchers,
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val dataBase: DataBase,
) : Job {

    private val coroutineScope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        coroutineScope.launch {
            clearAllCache()
            downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase()
            dataBase.demolishConnect()
        }
    }

    private suspend fun downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase() {
        restfulNotionDataBases.iterate().forEach { restfulNotionDb ->
            sqlLiteNotionDataBases.add(restfulNotionDb)
        }
    }

    private suspend fun clearAllCache() {
        sqlLiteNotionDataBases.clear()
    }
}