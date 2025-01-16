package org.danceofvalkyries.app.apps.notifier

import app.data.sqlite.notion.databases.SqlLiteNotionDataBases
import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.*
import org.danceofvalkyries.app.data.restful.notion.databases.RestFulNotionDataBases
import org.danceofvalkyries.app.data.sqlite.telegram.messages.SqlLiteTelegramMessages
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.utils.Dispatchers

class NotifierApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    private val config by lazy { environment.config }
    private val httpClient by lazy { environment.httpClient }

    override suspend fun run() {
        val dbConnection = environment.dataBase.establishConnection()
        val telegramApi = createTelegramChatApi()

        val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
        val restfulNotionDatabases = RestFulNotionDataBases(
            desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
            apiKey = environment.config.notion.apiKey,
            client = environment.httpClient,
            gson = Gson()
        )

        sqlLiteNotionDatabases.clear()
        restfulNotionDatabases.iterate().forEach { restfulNotionDb ->
            val sqlLiteNotionDb = sqlLiteNotionDatabases.add(restfulNotionDb)
            restfulNotionDb.iterate().forEach { restfulNotionPage ->
                sqlLiteNotionDb.add(restfulNotionPage)
            }
        }

        val telegramMessages = SqlLiteTelegramMessages(dbConnection)
        AnalyzeFlashCardsAndSendNotificationUseCase(
            sqlLiteNotionDatabases,
            EditNotificationMessageUseCase(
                telegramMessages,
                telegramApi
            ),
            DeleteOldAndSendNewNotificationUseCase(
                telegramApi,
                SendMessageToTelegramChat(telegramApi),
                SqlLiteTelegramMessages(dbConnection)
            ),
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createTelegramChatApi(): TelegramChatApi {
        return TelegramChatApiImpl(
            client = environment.httpClient,
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId
        )
    }

    private fun createGson(): Gson {
        return Gson()
    }
}