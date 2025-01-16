package org.danceofvalkyries.app.apps.notifier

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteTelegramMessagesType
import org.danceofvalkyries.app.data.users.bot.TelegramBotUser
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
    val telegramChat = RestfulTelegramChat(
        apiKey = environment.config.telegram.apiKey,
        client = environment.httpClient,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
    )
    val telegramBotUser = TelegramBotUser(
        telegramChat,
        sqlLiteNotionDatabases,
        sqlLiteTelegramMessages,
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
    private val telegramBotUser: TelegramBotUser,
) : App {

    override suspend fun run() {
        clearAllCache()
        downLoadNotionDataBasesAndPagesAndSaveToLocalDb()
        checkFlashCardsAndSendNotificationOrShowDoneMessage()
    }

    private suspend fun clearAllCache() {
        sqlLiteNotionDataBases.clear()
    }

    private suspend fun downLoadNotionDataBasesAndPagesAndSaveToLocalDb() {
        restfulNotionDataBases.iterate().forEach { restfulNotionDb ->
            val sqlLiteNotionDb = sqlLiteNotionDataBases.add(restfulNotionDb)
            restfulNotionDb.iterate().forEach { restfulNotionPage ->
                sqlLiteNotionDb.add(restfulNotionPage)
            }
        }
    }

    private suspend fun checkFlashCardsAndSendNotificationOrShowDoneMessage() {
        if (getAllFlashCardsNeedRevising().count() >= config.flashCardsThreshold) {
            telegramBotUser.deleteOldNotificationMessage()
            telegramBotUser.sendNewNotificationMessage()
        } else {
            telegramBotUser.editOldNotificationMessageToDoneMessage()
        }
    }

    private suspend fun getAllFlashCardsNeedRevising() = sqlLiteNotionDataBases.iterate().flatMap { it.iterate() }
}