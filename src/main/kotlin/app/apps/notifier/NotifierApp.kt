package org.danceofvalkyries.app.apps.notifier

import app.data.sqlite.notion.databases.SqlLiteNotionDataBases
import com.google.gson.Gson
import notion.impl.client.NotionClientApiImpl
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.*
import org.danceofvalkyries.app.data.sqlite.telegram.messages.SqlLiteTelegramMessages
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.NotionApiImpl
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

        val notionApi = NotionApi()
        val notionDatabases = SqlLiteNotionDataBases(dbConnection)

        val ids = config
            .notion
            .observedDatabases
            .map { NotionId(it.id) }
        val telegramMessages = SqlLiteTelegramMessages(dbConnection)
        ReplaceAllNotionCacheUseCase(
            ReplaceFlashCardsInCacheUseCase(
                ids,
                SqlLiteNotionDataBases(dbConnection),
                notionApi,
                dispatchers,
            ),
            ReplaceNotionDbsInCacheUseCase(
                ids,
                notionDatabases,
                notionApi,
                dispatchers,
            ),
            dispatchers
        ).execute()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            GetAllFlashCardsUseCase(
                notionDatabases,
            ),
            notionDatabases,
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

    private fun NotionApi(): NotionApi {
        return NotionApiImpl(
            NotionClientApiImpl(
                gson = createGson(),
                client = httpClient,
                apiKey = config.notion.apiKey,
            )
        )
    }
}