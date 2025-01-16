package org.danceofvalkyries.app.apps.buttonslistener

import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.SpaceRepetitionSession
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import org.danceofvalkyries.app.data.telegram_and_notion.sqlite.SqlLiteSentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

fun TelegramButtonListenerApp(
    dispatchers: Dispatchers,
    environment: Environment,
): App {
    val config = environment.config
    val dbConnection = environment.dataBase.establishConnection()
    val sqlLiteNotionDataBases = SqlLiteNotionDataBases(dbConnection)
    val restfulNotionDataBases = RestFulNotionDataBases(
        desiredDbIds = config.notion.observedDatabases.map { it.id },
        apiKey = config.notion.apiKey,
        client = environment.httpClient,
        gson = Gson(),
    )
    val sentNotionPageFlashCardsToTelegram = SqlLiteSentNotionPageFlashCardsToTelegram(dbConnection)
    val sqlLiteTelegramMessages = SqlLiteTelegramMessagesType(dbConnection)
    val onlineDictionaries = ConfigOnlineDictionaries(config.notion.observedDatabases)
    val telegramChat = RestfulTelegramChat(
        apiKey = config.telegram.apiKey,
        client = environment.httpClient,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
    )
    val botUser = TelegramBotUserImpl(
        telegramChat,
        sqlLiteNotionDataBases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
    )
    val humanUser = TelegramHumanUserImpl(
        localDbNotionDataBases = sqlLiteNotionDataBases,
        restfulNotionDataBases = restfulNotionDataBases,
    )
    val spaceRepetitionSession = SpaceRepetitionSession(
        humanUser,
        botUser,
    )
    return TelegramButtonListenerApp(
        spaceRepetitionSession,
        telegramChat,
    )
}

class TelegramButtonListenerApp(
    private val spaceRepetitionSession: SpaceRepetitionSession,
    private val telegramChat: TelegramChat,
) : App {

    override suspend fun run() {
        telegramChat.getEvents().onEach {
            when (val action = ButtonAction.parse(it.action.value)) {
                is ButtonAction.DataBase -> spaceRepetitionSession.beginFor(action.notionDbId)
                is ButtonAction.Forgotten -> spaceRepetitionSession.forget(action.flashCardId)
                is ButtonAction.Recalled -> spaceRepetitionSession.recall(action.flashCardId)
                is ButtonAction.Unknown -> Unit
            }
        }.onEach { it.answer() }.collectLatest(::println)
    }
}