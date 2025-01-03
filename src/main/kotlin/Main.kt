package org.danceofvalkyries

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okio.Path.Companion.toPath
import org.danceofvalkyries.notion.api.NotionDataBaseApi
import org.danceofvalkyries.notion.api.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.data.repositories.SpacedRepetitionDataBaseRepositoryImpl
import org.danceofvalkyries.telegram.api.TelegramApiImpl
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

private const val NOTION_API_VERSION = "2022-06-28"
private const val SPACED_REPETITION_CONFIG_PATH = "./spaced_repetition.config"

suspend fun main() {
    val config = Config(
        createGson(),
        getConfigJson(),
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
        buildMessage = { Message(it).toString() },
        sendMessage = {
            TelegramApiImpl(
                client = createOkHttpClient(),
                gson = createGson(),
                apiKey = config.telegram.apiKey
            ).sendMessage(config.telegram.chatId, it)
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

private fun getConfigJson(): String {
    return SPACED_REPETITION_CONFIG_PATH
        .toPath()
        .toFile()
        .readText()
}
