package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.usecases.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class NotifierApp(
    private val dispatchers: Dispatchers,
    private val dataBase: DataBase,
) : App {

    private val config: Config by lazy {
        LocalFileConfigRepository(
            createGson()
        ).getConfig()
    }

    override suspend fun run() {
        val telegramChatRepository = createTelegramChatRepository()
        val messageFactory = MessageFactoryImpl()
        val notionDatabasesRepository = createSpacedRepetitionDataBaseRepository()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            notionDatabasesRepository,
            EditNotificationMessageUseCase(telegramChatRepository),
            DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository
            ),
            messageFactory,
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createTelegramChatRepository(): TelegramChatRepository {
        val api = TelegramChatApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId,
        )
        val connection = dataBase.establishConnection()
        val db = TelegramNotificationMessageDbImpl(connection)
        return TelegramChatRepositoryImpl(api, db)
    }

    private fun createSpacedRepetitionDataBaseRepository(): SpacedRepetitionDataBaseRepository {
        return SpacedRepetitionDataBaseRepositoryImpl(
            config.notion.delayBetweenRequests.milliseconds,
            createNotionDataBasesApis(),
            dispatchers,
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
                databaseId = it,
                client = createHttpClient(),
                apiKey = config.notion.apiKey,
            )
        }
    }
}