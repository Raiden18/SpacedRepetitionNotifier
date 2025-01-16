package org.danceofvalkyries.app.apps

import app.data.sqlite.notion.databases.SqlLiteNotionDataBases
import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.restful.notion.databases.RestFulNotionDataBases
import org.danceofvalkyries.app.data.sqlite.telegram.messages.SqlLiteTelegramMessages
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

class SandBoxApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    private val notifier = NotifierApp(dispatchers, environment)

    @Suppress("UNREACHABLE_CODE")
    override suspend fun run() {
        val dbConnection = environment.dataBase.establishConnection()
        val telegramMessages: TelegramMessages = SqlLiteTelegramMessages(dbConnection)

        val restNotionDataBases = RestFulNotionDataBases(
            desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
            apiKey = environment.config.notion.apiKey,
            client = environment.httpClient,
            gson = Gson()
        )

        val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)

        notifier.run()

        /*environment.config.notion.observedDatabases.forEach {
            restNotionDataBases.add(it.id)
        }

        restNotionDataBases.iterate().forEach { notionDataBase ->
            notionDataBase.iterate().forEach { page ->
                println("HUETS")
                println(page)
            }
        }*/
    }
}