package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.usecases.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.app.domain.usecases.GetFlashCardsTablesUseCase
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.notion.data.repositories.NotionDbRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.domain.repositories.NotionDbRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.sql.Connection
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

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
        val notionDatabasesRepository = createSpacedRepetitionDataBaseRepository(dbConnection)
        AnalyzeFlashCardsAndSendNotificationUseCase(
            GetFlashCardsTablesUseCase(notionDatabasesRepository),
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

    private fun createSpacedRepetitionDataBaseRepository(dbConnection: Connection): NotionDbRepository {
        return NotionDbRepositoryImpl(
            TODO(),
            TODO(),
          /*  createNotionDataBasesApis(),
            dispatchers,
            FlashCardDbTableImpl(dbConnection)*/
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

    private fun createNotionDataBasesApis(): List<NotionDataBaseApi> {
        return config.notion.observedDatabases.map {
            NotionDataBaseApiImpl(
                gson = createGson(),
                client = createHttpClient(),
                apiKey = config.notion.apiKey,
            )
        }
    }
}