package org.danceofvalkyries.app.apps.notifier

import com.google.gson.Gson
import notion.impl.client.NotionClientApiImpl
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.*
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.app.data.sqlite.SqlLiteTelegramMessages
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
        val notionDataBaseDao = NotionDataBaseDaoImpl(dbConnection)
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(notionDataBaseDao)
        val notionApi = NotionApi()

        val notionPageFlashCardDataBaseTable = NotionPageFlashCardDataBaseTableImpl(
            NotionPageFlashCardDaoImpl(dbConnection)
        )
        val telegramMessageDao = TelegramMessageDaoImpl(dbConnection)
        val telegramMessagesDataBaseTable = TelegramMessagesDataBaseTableImpl(telegramMessageDao)
        val ids = config
            .notion
            .observedDatabases
            .map { NotionId(it.id) }
        ReplaceAllNotionCacheUseCase(
            ReplaceFlashCardsInCacheUseCase(
                ids,
                notionPageFlashCardDataBaseTable,
                notionApi,
                dispatchers,
            ),
            ReplaceNotionDbsInCacheUseCase(
                ids,
                notionDatabaseDataBaseTable,
                notionApi,
                dispatchers,
            ),
            dispatchers
        ).execute()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            GetAllFlashCardsUseCase(
                notionDatabaseDataBaseTable,
                notionPageFlashCardDataBaseTable,
            ),
            notionDatabaseDataBaseTable,
            EditNotificationMessageUseCase(
                telegramMessagesDataBaseTable,
                telegramApi
            ),
            DeleteOldAndSendNewNotificationUseCase(
                telegramMessagesDataBaseTable,
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