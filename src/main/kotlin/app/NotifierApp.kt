package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.repositories.flashcards.FlashCardsRepositoryImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.FlashCardDbTableImpl
import org.danceofvalkyries.app.data.repositories.telegram.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.app.domain.usecases.*
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import notion.impl.client.NotionApi
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApiImpl
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDataBaseDbTableImpl
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.GetDataBaseFromNotion
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi
import org.danceofvalkyries.telegram.impl.*
import org.danceofvalkyries.telegram.impl.restapi.TelegramChatRestApiImpl
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.sql.Connection
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
        val telegramChatRepository = createTelegramChatRepository(dbConnection)
        val messageFactory = MessageFactoryImpl()
        val notionDbsRepository = NotionDbRepository(dbConnection)
        val flashCardsRepository = FlashCardsRepository(dbConnection)
        val ids = config
            .notion
            .observedDatabases
            .map { NotionId(it.id) }
        ReplaceAllNotionCacheUseCase(
            ReplaceFlashCardsInCacheUseCase(
                ids,
                flashCardsRepository,
                dispatchers,
            ),
            ReplaceNotionDbsInCacheUseCase(
                ids,
                notionDbsRepository,
                GetDataBaseFromNotion(notionDbsRepository),
                dispatchers,
            ),
            dispatchers
        ).execute()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            GetAllFlashCardsUseCase(
                notionDbsRepository,
                flashCardsRepository,
            ),
            GetAllNotionDatabasesUseCase(notionDbsRepository),
            EditNotificationMessageUseCase(
                telegramChatRepository,
                EditMessageInTelegramChat(telegramChatRepository)
                ),
            DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository,
                DeleteMessageFromTelegramChat(telegramChatRepository),
                SendMessageToTelegramChat(telegramChatRepository)
            ),
            messageFactory,
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createTelegramChatRepository(dbConnection: Connection): TelegramChatApi {
        val api = TelegramChatRestApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
        )
        val db = TelegramNotificationMessageDbImpl(dbConnection)
        return TelegramChatApiImpl(api, db, config.telegram.chatId)
    }

    private fun NotionDbRepository(dbConnection: Connection): NotionDataBaseApi {
        return NotionDataBaseApiImpl(
            NotionApi(),
            NotionDataBaseDbTableImpl(dbConnection)
        )
    }

    private fun FlashCardsRepository(connection: Connection): FlashCardsRepository {
        return FlashCardsRepositoryImpl(
            FlashCardDbTableImpl(connection),
            NotionApi(),
            config,
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