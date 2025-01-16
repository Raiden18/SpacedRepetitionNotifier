package org.danceofvalkyries.app.apps

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.restful.notion.databases.RestNotionDataBases
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
        val telegramMessages: TelegramMessages = SqlLiteTelegramMessages(environment.dataBase.establishConnection())

        val restNotionDataBases = RestNotionDataBases(
            apiKey = environment.config.notion.apiKey,
            client = environment.httpClient,
            gson = Gson()
        )

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