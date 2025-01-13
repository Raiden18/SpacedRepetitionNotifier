package org.danceofvalkyries.app

import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.usecases.GetAllFlashCardsUseCase
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.impl.UpdatePageInNotion
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApiImpl
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.telegram.impl.TelegramChatApi
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.telegram.impl.restapi.TelegramChatRestApiImpl
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

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
        val telegramChatRepository = createTelegramChatRepository()
        val messageFactory = MessageFactoryImpl()
        val dbConnection = db.establishConnection()
        val notionApi = NotionApiImpl(
            gson = createGson(),
            client = createHttpClient(),
            apiKey = config.notion.apiKey,
        )
        val flashCardsTablesDbTable = NotionDataBaseDaoImpl(dbConnection)
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(flashCardsTablesDbTable)
        val telegramMessageDao = TelegramMessageDaoImpl(dbConnection)
        val telegramMessagesDataBaseTable = TelegramMessagesDataBaseTableImpl(telegramMessageDao)
        val flashCardNotionPageApi = FlashCardNotionPageApiImpl(notionApi)
        val notionPageFlashCardDao = NotionPageFlashCardDaoImpl(dbConnection)
        val notionPageFlashCardDataBaseTable = NotionPageFlashCardDataBaseTableImpl(
            notionPageFlashCardDao
        )

        ///realApp.run()


        /*  notionDatabaseDataBaseTable.getAll()
              .forEach {

                  repo.getAllFromDb(it.id)
                      .map {
                          val dictionaries = GetOnlineDictionariesForFlashCard(config.notion.observedDatabases).execute(it)
                          messageFactory.createFlashCardMessage(it, dictionaries)
                      }
                      .forEach {
                          SendMessageToTelegramChat(telegramChatRepository).execute(it)
                      }
              }*/


        /* while (true){
             val tgApi = TelegramChatApiImpl(
                 client = createHttpClient(),
                 gson = createGson(),
                 apiKey = config.telegram.apiKey,
                 chatId = config.telegram.chatId,
             )
             tgApi.getUpdate()
             delay(2.seconds)
         }*/

        //notionApi.recall()

        GetAllFlashCardsUseCase(
            NotionDatabaseDataBaseTableImpl(flashCardsTablesDbTable),
            NotionPageFlashCardDataBaseTableImpl(NotionPageFlashCardDaoImpl(dbConnection))
        ).execute()
            .forEach {
                UpdatePageInNotion(flashCardNotionPageApi).execute(it.recalled())
                /* ReplaceFlashCardInChatUseCase(
                     telegramMessagesDataBaseTable,
                     DeleteMessageFromTelegramChat(telegramChatRepository),
                     SendMessageToTelegramChat(telegramChatRepository),
                     GetOnlineDictionariesForFlashCard(config.notion.observedDatabases),
                     messageFactory,
                     dispatchers
                 ).execute(it)*/
                delay(1.seconds)
            }
    }

    private fun createTelegramChatRepository(): TelegramChatApi {
        val api = TelegramChatRestApiImpl(
            client = createHttpClient(),
            gson = createGson(),
            apiKey = config.telegram.apiKey,
        )
        return TelegramChatApiImpl(api, config.telegram.chatId)
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