package org.danceofvalkyries.job

import com.google.gson.Gson
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapConcat
import org.danceofvalkyries.bot.TelegramBot
import org.danceofvalkyries.bot.TelegramBotImpl
import org.danceofvalkyries.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.EngStringResources
import org.danceofvalkyries.utils.rest.clients.sever.KtorSeverClient

fun NotifierJob(
    dispatchers: Dispatchers,
    environment: Environment,
): Job {
    val dbConnection = environment.dataBase.establishConnection()
    val sqlLiteNotionDatabases = SqlLiteNotionDataBases(dbConnection)
    val httpClient = environment.httpClient
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
    return JobResourcesLifeCycleDecorator(
        dispatchers,
        httpClient,
        NotifierJob(
            environment.config.flashCardsThreshold,
            sqlLiteNotionDatabases,
            telegramBotUser,
        )
    )
}

class NotifierJob(
    private val flashCardsThreshold: Int,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val telegramBot: TelegramBot,
) : Job {

    override val type: String = "notifier"

    override suspend fun run() {
        println("HERE")
        checkFlashCardsAndSendNotificationOrShowDoneMessage()
        println("END")
    }

    private suspend fun checkFlashCardsAndSendNotificationOrShowDoneMessage() {
        if (getAllFlashCardsNeedRevising().count() >= flashCardsThreshold) {
            telegramBot.sendNotification()
        } else {
            telegramBot.editOldNotificationMessageToDoneMessage()
        }
    }

    private suspend fun getAllFlashCardsNeedRevising() = sqlLiteNotionDataBases.iterate().flatMapConcat { it.iterate() }
}