package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.repositories.flashcards.FlashCardsRepositoryImpl
import org.danceofvalkyries.app.data.repositories.flashcards.db.FlashCardDbTableImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.repositories.FlashCardsRepository
import org.danceofvalkyries.app.domain.usecases.*
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.api.NotionApiImpl
import org.danceofvalkyries.notion.data.repositories.database.NotionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.database.db.NotionDataBaseDbTableImpl
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.notion.domain.repositories.NotionDataBaseRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
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
            EditNotificationMessageUseCase(telegramChatRepository),
            DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository
            ),
            messageFactory,
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createTelegramChatRepository(dbConnection: Connection): TelegramChatRepository {
        val api = TelegramChatApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId,
        )
        val db = TelegramNotificationMessageDbImpl(dbConnection)
        return TelegramChatRepositoryImpl(api, db)
    }

    private fun NotionDbRepository(dbConnection: Connection): NotionDataBaseRepository {
        return NotionDataBaseRepositoryImpl(
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