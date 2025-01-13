package org.danceofvalkyries.app

import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.app.data.repositories.flashcards.FlashCardsRepositoryImpl
import org.danceofvalkyries.app.data.repositories.flashcards.db.FlashCardDbTableImpl
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.usecases.GetAllFlashCardsUseCase
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardInChatUseCase
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.notion.data.repositories.api.NotionApiImpl
import org.danceofvalkyries.notion.data.repositories.database.NotionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.database.db.NotionDataBaseDbTableImpl
import org.danceofvalkyries.notion.data.repositories.page.FlashCardNotionPageRepositoryImpl
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDbImpl
import org.danceofvalkyries.telegram.data.repositories.TelegramChatRepositoryImpl
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramImageUrl
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.db.DataBase
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

// TODO: Remove notion id from here
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
        val flashCardsTablesDbTable = NotionDataBaseDbTableImpl(dbConnection)
        val notionDbsRepository = NotionDataBaseRepositoryImpl(notionApi, flashCardsTablesDbTable)

        val flashCardsRepository = FlashCardsRepositoryImpl(
            FlashCardDbTableImpl(dbConnection),
            notionApi,
            config,
        )

        realApp.run()



        notionDbsRepository.getFromCache()
            .forEach {
                val repo = FlashCardNotionPageRepositoryImpl(notionApi)
                repo.getAllFromDb(it.id.get(NotionId.Modifier.URL_FRIENDLY))
                    .map {
                        FlashCard(
                            memorizedInfo = it.name,
                            example = it.example,
                            answer = it.explanation,
                            onlineDictionaries = emptyList(),
                            telegramImageUrl = null,
                            metaInfo = FlashCard.MetaInfo(
                                id = it.id.get(NotionId.Modifier.URL_FRIENDLY),
                                notionDbId = it.notionDbID.get(NotionId.Modifier.URL_FRIENDLY)
                            )
                        )
                    }.map { messageFactory.createFlashCardMessage(it) }
                    .forEach {
                        telegramChatRepository.sendToChat(it)
                    }
            }


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
            notionDbsRepository,
            flashCardsRepository
        ).execute()
            .forEach {
                ReplaceFlashCardInChatUseCase(
                    telegramChatRepository,
                    messageFactory,
                    dispatchers
                ).execute(it)
                delay(1.seconds)
            }
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