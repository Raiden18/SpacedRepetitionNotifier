package org.danceofvalkyries.app

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.telegram.impl.client.TelegramChatRestApiImpl
import org.danceofvalkyries.utils.Dispatchers
import java.util.concurrent.TimeUnit

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    private val config: Config by lazy { environment.config }

    override suspend fun run() {
        val dbConnection = environment.dataBase.establishConnection()
        val telegramApi = TelegramChatApiImpl(
            TelegramChatRestApiImpl(
                client = environment.httpClient,
                gson = createGson(),
                apiKey = config.telegram.apiKey,
            ),
            config.telegram.chatId
        )

        val notionApi = NotionApiImpl(
            gson = createGson(),
            client = environment.httpClient,
            apiKey = config.notion.apiKey,
        )
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(NotionDataBaseDaoImpl(dbConnection))
        val notionDataBaseApi = NotionDataBaseApiImpl(notionApi)

        val notionPageFlashCardDao = NotionPageFlashCardDaoImpl(dbConnection)
        val notionPageFlashCardDataBaseTable = NotionPageFlashCardDataBaseTableImpl(notionPageFlashCardDao)

        val telegramMessageDao = TelegramMessageDaoImpl(dbConnection)
        val telegramMessagesDataBaseTable = TelegramMessagesDataBaseTableImpl(telegramMessageDao)

        embeddedServer(Netty, port = 8080) {
            routing {
                post("/webhook") {
                    val update = call.receiveText()
                    println("Received update: $update") // Log the update for debugging
                    call.respond(HttpStatusCode.OK)
                }
            }
        }.start(wait = true)
    }

    private fun createGson(): Gson {
        return Gson()
    }
}