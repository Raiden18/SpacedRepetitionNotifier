package org.danceofvalkyries.app.apps.notifier

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.HttpClientImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.KtorWebServerImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.bot.translator.TelegramTextTranslator
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.EngStringResources

fun NotifierApp(
    dispatchers: Dispatchers,
    environment: Environment,
): App {
    val dbConnection = environment.dataBase.establishConnection()
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    val httpClient = HttpClientImpl(environment.httpClient)
    val restfulNotionDatabases = RestFulNotionDataBases(
        desiredDbIds = environment.config.notion.observedDatabases.map { it.id },
        apiKey = environment.config.notion.apiKey,
        httpClient = httpClient,
        gson = Gson()
    )
    val sqlLiteTelegramMessages = SqlLiteSentTelegramMessagesType(dbConnection)
    val webServer = KtorWebServerImpl()
    val telegramChat = RestfulTelegramChat(
        apiKey = environment.config.telegram.apiKey,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
        ktorWebServer = webServer,
        httpClient = httpClient,
    )
    val onlineDictionaries = ConfigOnlineDictionaries(environment.config.notion.observedDatabases)
    val telegramBotUser = TelegramBotUserImpl(
        telegramChat,
        sqlLiteNotionDatabases,
        restfulNotionDatabases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
        TelegramTextTranslator(),
        EngStringResources(),
    )
    return NotifierApp(
        dispatchers,
        environment.config.flashCardsThreshold,
        restfulNotionDatabases,
        sqlLiteNotionDatabases,
        telegramBotUser,
    )
}

class NotifierApp(
    private val dispatchers: Dispatchers,
    private val flashCardsThreshold: Int,
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val telegramBot: TelegramBotUser,
) : App {

    private val coroutineScope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        coroutineScope.launch {
            clearAllCache()
            downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase()
            checkFlashCardsAndSendNotificationOrShowDoneMessage()
        }
    }

    private suspend fun clearAllCache() {
        sqlLiteNotionDataBases.clear()
    }

    private suspend fun downLoadNotionDataBasesAndPagesAndSaveThemToLocalDataBase() {
        restfulNotionDataBases.iterate().forEach { restfulNotionDb ->
            sqlLiteNotionDataBases.add(restfulNotionDb)
        }
    }

    private suspend fun checkFlashCardsAndSendNotificationOrShowDoneMessage() {
        if (getAllFlashCardsNeedRevising().count() >= flashCardsThreshold) {
            telegramBot.deleteOldNotificationMessage()
            telegramBot.sendNewNotificationMessage()
        } else {
            telegramBot.editOldNotificationMessageToDoneMessage()
        }
    }

    private suspend fun getAllFlashCardsNeedRevising() = sqlLiteNotionDataBases.iterate().flatMap { it.iterate() }
}