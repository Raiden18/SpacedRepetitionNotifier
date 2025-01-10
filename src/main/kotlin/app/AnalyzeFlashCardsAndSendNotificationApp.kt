package org.danceofvalkyries.app

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.domain.*
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.EnvironmentImpl
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiTelegramMessageErrorLoggerDecorator
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiTimeMeasurePerfomanceDecorator
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.domain.models.DoneMessage
import org.danceofvalkyries.telegram.domain.models.RevisingIsNeededMessage
import org.danceofvalkyries.utils.DispatchersImpl
import org.danceofvalkyries.utils.db.DataBasePaths
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class AnalyzeFlashCardsAndSendNotificationApp : App {

    private val environment by lazy { EnvironmentImpl() }
    private val dispatchers by lazy { DispatchersImpl(Dispatchers.IO) }
    private val gson by lazy { Gson() }
    private val httpClient by lazy {
        val logger = HttpLoggingInterceptor.Logger { message -> println(message) }
        val interceptor = HttpLoggingInterceptor(logger)
        val timeOut = 60_000L
        OkHttpClient.Builder()
            .callTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .build()
    }
    private val telegramMessagesDb by lazy {
        val dbPaths = DataBasePaths(environment.homeDirectory)
        val connection = DriverManager.getConnection("jdbc:sqlite:${dbPaths.production()}")
        TelegramNotificationMessageDbImpl(connection)
    }

    private val config: Config by lazy {
        TestConfigRepository(gson).getConfig()
    }

    private val telegramChatApi by lazy {
        TelegramChatApiImpl(
            client = httpClient,
            gson = gson,
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId,
        )
    }

    private val spacedRepetitionDataBaseRepository by lazy {
        SpacedRepetitionDataBaseRepositoryImpl(
            config.notion.delayBetweenRequests.milliseconds,
            createNotionDataBasesApis(
                gson,
                httpClient,
                config,
                telegramChatApi,
            ),
            dispatchers,
        )
    }

    override suspend fun run() {
        sendReviseOrDoneMessage(
            spacedRepetitionDataBaseRepository.getAll(),
            config.flashCardsThreshold,
            ::deleteOldAndSendNewNotification,
            ::sendGoodJobMessage,
        )
    }

    private fun createNotionDataBasesApis(
        gson: Gson,
        okHttpClient: OkHttpClient,
        config: Config,
        telegramChatApi: TelegramChatApi,
    ): List<NotionDataBaseApi> {
        return config.notion.observedDatabases.map {
            NotionDataBaseApiImpl(
                gson = gson,
                databaseId = it,
                client = okHttpClient,
                apiKey = config.notion.apiKey,
            )
        }.map { NotionDataBaseApiTimeMeasurePerfomanceDecorator(it) }
            .map { NotionDataBaseApiTelegramMessageErrorLoggerDecorator(it, telegramChatApi) }
    }

    private suspend fun deleteOldAndSendNewNotification(group: SpacedRepetitionDataBaseGroup) {
        deleteOldAndSendNewNotification(
            telegramChatApi,
            telegramMessagesDb,
            RevisingIsNeededMessage(group),
        )
    }

    private suspend fun sendGoodJobMessage() {
        editNotificationMessage(DoneMessage().text, telegramMessagesDb, telegramChatApi)
    }
}