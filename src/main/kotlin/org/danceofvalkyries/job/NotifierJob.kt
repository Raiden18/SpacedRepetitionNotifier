package org.danceofvalkyries.job

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.job.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.job.data.notion.databases.NotionDataBases
import org.danceofvalkyries.job.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.job.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.job.data.telegram.bot.TelegramBot
import org.danceofvalkyries.job.data.telegram.bot.TelegramBotImpl
import org.danceofvalkyries.job.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.job.data.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.job.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.EngStringResources
import org.danceofvalkyries.utils.rest.clients.http.HttpClientImpl
import org.danceofvalkyries.utils.rest.clients.sever.KtorSeverClient

fun NotifierJob(
    dispatchers: Dispatchers,
    environment: Environment,
): Job {
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
    val webServer = KtorSeverClient()
    val telegramChat = RestfulTelegramChat(
        apiKey = environment.config.telegram.apiKey,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
        severClient = webServer,
        httpClient = httpClient,
    )
    val onlineDictionaries = ConfigOnlineDictionaries(environment.config.notion.observedDatabases)
    val telegramBotUser = TelegramBotImpl(
        telegramChat,
        sqlLiteNotionDatabases,
        restfulNotionDatabases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
        EngStringResources(),
        dispatchers,
    )
    return NotifierJob(
        dispatchers,
        environment.config.flashCardsThreshold,
        sqlLiteNotionDatabases,
        telegramBotUser,
    )
}

class NotifierJob(
    private val dispatchers: Dispatchers,
    private val flashCardsThreshold: Int,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val telegramBot: TelegramBot,
) : Job {

    private val coroutineScope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        coroutineScope.launch {
            checkFlashCardsAndSendNotificationOrShowDoneMessage()
        }
    }

    private suspend fun checkFlashCardsAndSendNotificationOrShowDoneMessage() {
        if (getAllFlashCardsNeedRevising().count() >= flashCardsThreshold) {
            telegramBot.sendNotification()
        } else {
            telegramBot.editOldNotificationMessageToDoneMessage()
        }
    }

    private suspend fun getAllFlashCardsNeedRevising() = sqlLiteNotionDataBases.iterate().flatMap { it.iterate() }
}