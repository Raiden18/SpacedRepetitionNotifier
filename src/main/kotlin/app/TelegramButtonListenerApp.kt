package org.danceofvalkyries.app

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramText
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.telegram.impl.client.TelegramChatRestApiImpl
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.util.concurrent.TimeUnit

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val dataBase: DataBase,
    private val configRepository: ConfigRepository = TestConfigRepository()
) : App {

    private val config: Config by lazy {
        configRepository.getConfig()
    }

    override suspend fun run() {
        val dbConnection = dataBase.establishConnection()
        val telegramApi = TelegramChatApiImpl(
            TelegramChatRestApiImpl(
                client = createHttpClient(),
                gson = createGson(),
                apiKey = config.telegram.apiKey,
            ),
            config.telegram.chatId
        )

        val notionApi = NotionApiImpl(
            gson = createGson(),
            client = createHttpClient(),
            apiKey = config.notion.apiKey,
        )
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(NotionDataBaseDaoImpl(dbConnection))
        val notionDataBaseApi = NotionDataBaseApiImpl(notionApi)

        val notionPageFlashCardDao = NotionPageFlashCardDaoImpl(dbConnection)
        val notionPageFlashCardDataBaseTable = NotionPageFlashCardDataBaseTableImpl(notionPageFlashCardDao)


        val messageFactory = MessageFactoryImpl()

        val telegramMessageDao = TelegramMessageDaoImpl(dbConnection)
        val telegramMessagesDataBaseTable = TelegramMessagesDataBaseTableImpl(telegramMessageDao)

        embeddedServer(Netty, port = 8080) {
            routing {
                post("/webhook") {
                    println("HUETS Webhook hit")
                    val update = call.receiveText()
                    println("Received update: $update") // Log the update for debugging
                    telegramApi.sendTextMessage(
                        TelegramMessageBody(
                            text = TelegramText("Received message"),
                            nestedButtons = emptyList(),
                            imageUrl = null,
                            type = TelegramMessageBody.Type.UNKNOWN,
                        )
                    )
                    call.respond(HttpStatusCode.OK)
                }
            }
        }.start(wait = true)
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
}