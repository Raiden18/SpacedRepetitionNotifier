package org.danceofvalkyries.app

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.data.repositories.FlashCardsTablesRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApi
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.data.repositories.api.NotionDataBaseApiTimeMeasurePerfomanceDecorator
import org.danceofvalkyries.notion.data.repositories.db.FlashCardDbTableImpl
import org.danceofvalkyries.notion.domain.repositories.FlashCardsTablesRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.sql.Connection
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class TestApp(
    private val db: DataBase,
    private val dispatchers: Dispatchers,
) : App {

    private val config: Config by lazy {
        TestConfigRepository().getConfig()
    }

    private val realApp by lazy {
        NotifierApp(
            dispatchers,
            db,
            TestConfigRepository()
        )
    }

    override suspend fun run() {
      /*  val telegramChatRepository = createTelegramChatRepository()
        val messageFactory = MessageFactoryImpl()
        val dbConnection = db.establishConnection()

        val notionDatabasesRepository = createSpacedRepetitionDataBaseRepository(dbConnection)


        val dataBases = notionDatabasesRepository.getFromNotion()*/

        realApp.run()


    }

    private fun createTelegramChatRepository(): TelegramChatRepository {
        val api = TelegramChatApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId,
        )
        val connection = db.establishConnection()
        val db = TelegramNotificationMessageDbImpl(connection)
        return TelegramChatRepositoryImpl(api, db)
    }

    private fun createSpacedRepetitionDataBaseRepository(dbConnection: Connection): FlashCardsTablesRepository {
        return FlashCardsTablesRepositoryImpl(
            config.notion.delayBetweenRequests.milliseconds,
            createNotionDataBasesApis(),
            dispatchers,
            FlashCardDbTableImpl(dbConnection)
        )
    }

    private fun createHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor.Logger { message -> println(message) }
        val interceptor = HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
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
}