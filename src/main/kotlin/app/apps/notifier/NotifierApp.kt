package org.danceofvalkyries.app.apps.notifier

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.KtorWebServerImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.chat.restful.TelegramChatHttpClient
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.Environment

fun NotifierApp(
    environment: Environment,
): App {
    val dbConnection = environment.dataBase.establishConnection()
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    val restfulNotionDatabases = RestFulNotionDataBases(
        desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
        apiKey = environment.config.notion.apiKey,
        client = environment.httpClient,
        gson = Gson()
    )
    val sqlLiteTelegramMessages = SqlLiteTelegramMessagesType(dbConnection)
    val webServer = KtorWebServerImpl()
    val telegramChat = RestfulTelegramChat(
        apiKey = environment.config.telegram.apiKey,
        client = environment.httpClient,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
        ktorWebServer = webServer,
        httpClient = TelegramChatHttpClient(environment.httpClient)
    )
    val onlineDictionaries = ConfigOnlineDictionaries(environment.config.notion.observedDatabases)
    val telegramBotUser = TelegramBotUserImpl(
        telegramChat,
        sqlLiteNotionDatabases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
    )
    return NotifierApp(
        environment.config,
        restfulNotionDatabases,
        sqlLiteNotionDatabases,
        telegramBotUser,
    )
}

class NotifierApp(
    private val config: Config,
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val telegramBot: TelegramBotUser,
) : App {

    override suspend fun run() {
        clearAllCache()
        downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase()
        checkFlashCardsAndSendNotificationOrShowDoneMessage()
    }

    private suspend fun clearAllCache() {
        sqlLiteNotionDataBases.clear()
    }

    private suspend fun downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase() {
        restfulNotionDataBases.iterate().forEach { restfulNotionDb ->
            val sqlLiteNotionDb = sqlLiteNotionDataBases.add(restfulNotionDb)
            restfulNotionDb.iterate().forEach { restfulNotionPage ->
                sqlLiteNotionDb.add(restfulNotionPage)
            }
        }
    }

    private suspend fun checkFlashCardsAndSendNotificationOrShowDoneMessage() {
        if (getAllFlashCardsNeedRevising().count() >= config.flashCardsThreshold) {
            telegramBot.deleteOldNotificationMessage()
            telegramBot.sendNewNotificationMessage()
        } else {
            telegramBot.editOldNotificationMessageToDoneMessage()
        }
    }

    private suspend fun getAllFlashCardsNeedRevising() = sqlLiteNotionDataBases.iterate().flatMap { it.iterate() }
}