package org.danceofvalkyries.app.apps.buttonslistener

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.restful.HttpClientImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.KtorWebServerImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.bot.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
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
    val httpClient = HttpClientImpl(environment.httpClient)
    val restfulNotionDataBases = RestFulNotionDataBases(
        desiredDbIds = config.notion.observedDatabases.map { it.id },
        apiKey = config.notion.apiKey,
        httpClient = httpClient,
        gson = Gson(),
    )
    val sqlLiteTelegramMessages = SqlLiteSentTelegramMessagesType(dbConnection)
    val onlineDictionaries = ConfigOnlineDictionaries(config.notion.observedDatabases)
    val webServer = KtorWebServerImpl()

    val telegramChat = RestfulTelegramChat(
        apiKey = config.telegram.apiKey,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
        ktorWebServer = webServer,
        httpClient = httpClient,
    )
    val botUser = TelegramBotUserImpl(
        telegramChat,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
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
        dispatchers,
        spaceRepetitionSession,
        telegramChat,
    )
}

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val spaceRepetitionSession: SpaceRepetitionSession,
    private val telegramChat: TelegramChat,
) : App {

    private val scope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        scope.launch {
            telegramChat
                .getEvents()
                .collect {
                    when (val action = ButtonAction.parse(it.action.value)) {
                        is ButtonAction.DataBase -> spaceRepetitionSession.beginFor(action.notionDbId)
                        is ButtonAction.Forgotten -> spaceRepetitionSession.forget(action.flashCardId)
                        is ButtonAction.Recalled -> spaceRepetitionSession.recall(action.flashCardId)
                        is ButtonAction.Unknown -> telegramChat.delete(it.messageId)
                    }
                }
        }
    }
}