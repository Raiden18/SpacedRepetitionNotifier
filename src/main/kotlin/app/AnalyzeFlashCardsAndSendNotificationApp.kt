package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.usecases.AnalyzeFlashCardsAndSendNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiTimeMeasurePerfomanceDecorator
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBasePaths
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class AnalyzeFlashCardsAndSendNotificationApp(
    private val environment: Environment,
    private val dispatchers: Dispatchers,
    private val configRepository: ConfigRepository,
) : App {

    private val config: Config by lazy {
        configRepository.getConfig()
    }

    override suspend fun run() {
        val telegramChatRepository = createTelegramChatRepository()
        AnalyzeFlashCardsAndSendNotificationUseCase(
            createSpacedRepetitionDataBaseRepository(),
            EditNotificationMessageUseCase(telegramChatRepository),
            DeleteOldAndSendNewNotificationUseCase(
                telegramChatRepository
            ),
            MessageFactoryImpl(),
            config.flashCardsThreshold,
        ).execute()
    }

    private fun createNotionDataBasesApis(): List<NotionDataBaseApi> {
        return config.notion.observedDatabases.map {
            NotionDataBaseApiImpl(
                gson = createGson(),
                databaseId = it,
                client = createHttpClient(),
                apiKey = config.notion.apiKey,
            )
        }.map { NotionDataBaseApiTimeMeasurePerfomanceDecorator(it) }
    }

    private fun createTelegramChatRepository(): TelegramChatRepository {
        val api = TelegramChatApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId,
        )
        val dbPaths = DataBasePaths(environment.homeDirectory)
        val connection = DriverManager.getConnection("jdbc:sqlite:${dbPaths.production()}")
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
        val logger = HttpLoggingInterceptor.Logger { message -> println(message) }
        val interceptor = HttpLoggingInterceptor(logger)
        val timeOut = 60_000L
        return OkHttpClient.Builder()
            .callTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    private fun createGson(): Gson {
        return Gson()
    }
}