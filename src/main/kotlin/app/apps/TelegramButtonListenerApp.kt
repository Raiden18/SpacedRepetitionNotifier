package org.danceofvalkyries.app.apps

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.restful.HttpClientImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.KtorWebServerImpl
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage.Button
import org.danceofvalkyries.app.data.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.bot.TelegramBot
import org.danceofvalkyries.app.data.telegram.bot.TelegramBotImpl
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.EngStringResources

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
    val botUser = TelegramBotImpl(
        telegramChat,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
        TelegramTextTranslator(),
        EngStringResources(),
        dispatchers,
    )
    return TelegramButtonListenerApp(
        dispatchers,
        telegramChat,
        botUser,
    )
}

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val telegramChat: TelegramChat,
    private val bot: TelegramBot,
) : App {

    private val scope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        scope.launch {
            telegramChat
                .getEvents()
                .onEach {
                    when (val action = ButtonAction.parse(it.action.value)) {
                        is ButtonAction.DataBase -> bot.startRepetitionSessionFor(action.notionDbId)
                        is ButtonAction.Forgotten -> bot.makeForgotten(action.flashCardId)
                        is ButtonAction.Recalled -> bot.makeRecalled(action.flashCardId)
                        is ButtonAction.Unknown -> bot.deleteMessage(it.messageId)
                    }
                }.collect(Button.Callback::answer)
        }
    }
}