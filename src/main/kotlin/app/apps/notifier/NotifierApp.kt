package org.danceofvalkyries.app.apps.notifier

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.EditNotificationMessageUseCase
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.TelegramMessages
import org.danceofvalkyries.app.data.telegram.sqlite.SqlLiteTelegramMessages
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.utils.Dispatchers

fun NotifierApp(
    dispatchers: Dispatchers,
    environment: Environment,
): App {
    val dbConnection = environment.dataBase.establishConnection()
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    val restfulNotionDatabases = RestFulNotionDataBases(
        desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
        apiKey = environment.config.notion.apiKey,
        client = environment.httpClient,
        gson = Gson()
    )
    val sqlLiteTelegramMessages = SqlLiteTelegramMessages(dbConnection)
    return NotifierApp(
        dispatchers,
        environment,
        restfulNotionDatabases,
        sqlLiteNotionDatabases,
        sqlLiteTelegramMessages
    )
}

class NotifierApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val sqlLiteTelegramMessages: TelegramMessages,
) : App {

    private val config by lazy { environment.config }

    override suspend fun run() {
        val telegramApi = createTelegramChatApi()
        clearDataBase()
        downLoadNotionDbsAndPagesAndSaveToLocalDb()

        AnalyzeFlashCardsAndSendNotificationUseCase(
            sqlLiteNotionDataBases,
            EditNotificationMessageUseCase(
                sqlLiteTelegramMessages,
                telegramApi
            ),
            DeleteOldAndSendNewNotificationUseCase(
                telegramApi,
                SendMessageToTelegramChat(telegramApi),
                sqlLiteTelegramMessages
            ),
            config.flashCardsThreshold,
        ).execute()
    }

    private suspend fun clearDataBase() {
        sqlLiteNotionDataBases.clear()
    }

    private suspend fun downLoadNotionDbsAndPagesAndSaveToLocalDb() {
        restfulNotionDataBases.iterate().forEach { restfulNotionDb ->
            val sqlLiteNotionDb = sqlLiteNotionDataBases.add(restfulNotionDb)
            restfulNotionDb.iterate().forEach { restfulNotionPage ->
                sqlLiteNotionDb.add(restfulNotionPage)
            }
        }
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