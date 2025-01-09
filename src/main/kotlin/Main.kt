package org.danceofvalkyries

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.AppImpl
import org.danceofvalkyries.app.AppMeasurePerfomanceDecorator
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.message.goodJobMessage
import org.danceofvalkyries.message.revisingIsNeededMessage
import org.danceofvalkyries.notion.api.NotionDataBaseApi
import org.danceofvalkyries.notion.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.api.NotionDataBaseApiTimeMeasurePerfomanceDecorator
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryMeasurePerfomanceDecorator
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramMessagesDbImpl
import org.danceofvalkyries.telegram.domain.deleteOldMessages
import org.danceofvalkyries.telegram.domain.replaceNotificationMessage
import org.danceofvalkyries.telegram.domain.sendMessageToChatAndSaveToDb
import org.danceofvalkyries.telegram.domain.updateNotificationMessage
import org.danceofvalkyries.utils.db.DataBasePaths
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

// Before
// 1 app time spent: 14.893309s
// 2 app time spent: 17.852882300s
// 3 app time spent: 21.232378700s


suspend fun main() {
    val dbPaths = DataBasePaths(
        homeDirectory = System.getProperty("user.home")
    )
    val connection = DriverManager.getConnection("jdbc:sqlite:${dbPaths.production()}")
    val telegramMessagesDbTable = TelegramMessagesDbImpl(
        connection
    )

    val gson = Gson()
    val httpClient = createOkHttpClient()

    val configRepository = TestConfigRepository(gson)

    val config = configRepository.getConfig()

    val telegramChatApi = TelegramChatApiImpl(
        client = httpClient,
        gson = gson,
        apiKey = config.telegram.apiKey,
        chatId = config.telegram.chatId,
    )

    val app = AppMeasurePerfomanceDecorator(
        AppImpl(
            spacedRepetitionDataBaseRepository = SpacedRepetitionDataBaseRepositoryMeasurePerfomanceDecorator(
                SpacedRepetitionDataBaseRepositoryImpl(
                    config.notion.delayBetweenRequests.milliseconds,
                    NotionDataBasesApis(
                        gson,
                        httpClient,
                        config,
                    ),
                )
            ),
            flashCardsThreshold = config.flashCardsThreshold,
            sendGoodJobMessage = {
                updateNotificationMessage.invoke(
                    goodJobMessage.invoke(),
                    telegramMessagesDbTable,
                    telegramChatApi,
                )
            },
            sendRevisingMessage = {
                replaceNotificationMessage(
                    { deleteOldMessages(telegramChatApi, telegramMessagesDbTable) },
                    {
                        sendMessageToChatAndSaveToDb(
                            telegramChatApi,
                            telegramMessagesDbTable,
                            revisingIsNeededMessage.invoke(it)
                        )
                    }
                )
            },
        )
    )
    app.run()
}

private fun NotionDataBasesApis(
    gson: Gson,
    okHttpClient: OkHttpClient,
    config: Config,
): List<NotionDataBaseApi> {
    return config.notion.observedDatabases.map {
        NotionDataBaseApiImpl(
            gson = gson,
            databaseId = it,
            client = okHttpClient,
            apiKey = config.notion.apiKey,
        )
    }.map {
        NotionDataBaseApiTimeMeasurePerfomanceDecorator(it)
    }
}

private fun createOkHttpClient(): OkHttpClient {
    val timeOut = 30_000L
    return OkHttpClient.Builder()
        .callTimeout(timeOut, TimeUnit.MILLISECONDS)
        .readTimeout(timeOut, TimeUnit.MILLISECONDS)
        .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
        .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
        .build()
}
