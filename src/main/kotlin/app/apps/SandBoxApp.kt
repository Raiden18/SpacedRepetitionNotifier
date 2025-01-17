package org.danceofvalkyries.app.apps

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.HttpClientImpl
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
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
        val sentTelegramMessagesType: SentTelegramMessagesType = SqlLiteSentTelegramMessagesType(dbConnection)

        val restNotionDataBases = RestFulNotionDataBases(
            desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
            apiKey = environment.config.notion.apiKey,
            httpClient = HttpClientImpl(environment.httpClient),
            gson = Gson()
        )

        val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)

        notifier.run()

        //notifier.run()

        /*environment.config.notion.observedDatabases.forEach {
            restNotionDataBases.add(it.id)
        }

        restNotionDataBases.iterate().forEach { notionDataBase ->
            notionDataBase.iterate().forEach { page ->

            }
        }*/
    }
}