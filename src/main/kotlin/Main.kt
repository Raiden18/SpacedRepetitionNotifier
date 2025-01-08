package org.danceofvalkyries

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okio.Path.Companion.toPath
import org.danceofvalkyries.utils.db.DataBasePaths
import org.danceofvalkyries.json.`object`
import org.danceofvalkyries.message.goodJobMessage
import org.danceofvalkyries.message.revisingIsNeededMessage
import org.danceofvalkyries.notion.api.NotionDataBaseApi
import org.danceofvalkyries.notion.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.telegram.data.api.TelegramChatApiImpl
import org.danceofvalkyries.telegram.data.db.TelegramMessagesDbImpl
import org.danceofvalkyries.telegram.domain.deleteOldMessages
import org.danceofvalkyries.telegram.domain.replaceNotificationMessage
import org.danceofvalkyries.telegram.domain.sendMessageToChatAndSaveToDb
import org.danceofvalkyries.telegram.domain.updateNotificationMessage
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

private const val NOTION_API_VERSION = "2022-06-28"
private const val SPACED_REPETITION_CONFIG_PATH = "./spaced_repetition.config"

suspend fun main() {
    val dbPaths = DataBasePaths(
        homeDirectory = System.getProperty("user.home")
    )
    val connection = DriverManager.getConnection("jdbc:sqlite:${dbPaths.development()}")
    val telegramMessagesDbTable = TelegramMessagesDbImpl(
        connection
    )

    val config = Config(
        createGson(),
        getConfigJson(createGson()),
    )
    val telegramChatApi = TelegramChatApiImpl(
        client = createOkHttpClient(),
        gson = createGson(),
        apiKey = config.telegram.apiKey,
        chatId = config.telegram.chatId,
    )

    SpaceRepetitionTelegramReminderApp(
        spacedRepetitionDataBaseRepository = SpacedRepetitionDataBaseRepositoryImpl(
            config.notion.delayBetweenRequests.milliseconds,
            NotionDataBasesApis(
                createGson(),
                createOkHttpClient(),
                config,
            ),
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
    ).run()
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
            apiVersion = NOTION_API_VERSION,
            apiKey = config.notion.apiKey,
        )
    }
}

private fun createGson(): Gson {
    return Gson()
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

private fun getConfigJson(gson: Gson): String {
    return SPACED_REPETITION_CONFIG_PATH
        .toPath()
        .toFile()
        .readText()
}
