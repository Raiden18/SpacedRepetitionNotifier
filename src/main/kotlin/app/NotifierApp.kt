package org.danceofvalkyries.app

import com.google.gson.Gson
import notion.impl.client.NotionApi
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.usecases.*
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.GetAllPagesFromNotionDataBase
import org.danceofvalkyries.notion.impl.GetDataBaseFromNotion
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApiImpl
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.telegram.impl.*
import org.danceofvalkyries.telegram.impl.restapi.TelegramChatRestApiImpl
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.util.concurrent.TimeUnit

class NotifierApp(
    private val dispatchers: Dispatchers,
    private val dataBase: DataBase,
    private val configRepository: ConfigRepository = LocalFileConfigRepository()
) : App {

    private val config: Config by lazy {
        configRepository.getConfig()
    }

    override suspend fun run() {
        val dbConnection = dataBase.establishConnection()
        val telegramChatRepository = createTelegramChatApi()
        val messageFactory = MessageFactoryImpl()
        val notionDataBaseDao = NotionDataBaseDaoImpl(dbConnection)
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(notionDataBaseDao)
        val notionDbsRepository = NotionDbRepository()
        val flashCardNotionPageApi = FlashCardNotionPageApiImpl(
            NotionApi()
        )

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
                GetAllPagesFromNotionDataBase(flashCardNotionPageApi),
                dispatchers,
            ),
            ReplaceNotionDbsInCacheUseCase(
                ids,
                notionDatabaseDataBaseTable,
                GetDataBaseFromNotion(notionDbsRepository),
                dispatchers,
            ),
            dispatchers
        ).execute()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            GetAllFlashCardsUseCase(
                notionDatabaseDataBaseTable,
                notionPageFlashCardDataBaseTable,
            ),
            GetAllNotionDatabasesUseCase(notionDatabaseDataBaseTable),
            EditNotificationMessageUseCase(
                telegramMessagesDataBaseTable,
                EditMessageInTelegramChat(telegramChatRepository)
            ),
            DeleteOldAndSendNewNotificationUseCase(
                telegramMessagesDataBaseTable,
                DeleteMessageFromTelegramChat(telegramChatRepository),
                SendMessageToTelegramChat(telegramChatRepository)
            ),
            messageFactory,
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createTelegramChatApi(): TelegramChatApi {
        val api = TelegramChatRestApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
        )
        return TelegramChatApiImpl(api, config.telegram.chatId)
    }

    private fun NotionDbRepository(): NotionDataBaseApi {
        return NotionDataBaseApiImpl(
            NotionApi()
        )
    }

    private fun createHttpClient(): OkHttpClient {
        val timeOut = 60_000L
        return OkHttpClient.Builder()
            .callTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun createGson(): Gson {
        return Gson()
    }

    private fun NotionApi(): NotionApi {
        return NotionApiImpl(
            gson = createGson(),
            client = createHttpClient(),
            apiKey = config.notion.apiKey,
        )
    }
}