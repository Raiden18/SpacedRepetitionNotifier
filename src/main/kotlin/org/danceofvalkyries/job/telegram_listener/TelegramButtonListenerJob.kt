package org.danceofvalkyries.job.telegram_listener

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.job.Job
import org.danceofvalkyries.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.telegram.bot.TelegramBot
import org.danceofvalkyries.telegram.bot.TelegramBotImpl
import org.danceofvalkyries.telegram.chat.TelegramChat
import org.danceofvalkyries.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.EngStringResources
import org.danceofvalkyries.utils.rest.clients.http.HttpClientImpl
import org.danceofvalkyries.utils.rest.clients.sever.KtorSeverClient

fun ListenToTelegramEvensJob(
    dispatchers: Dispatchers,
    environment: Environment,
): Job {
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
    val webServer = KtorSeverClient()

    val telegramChat = RestfulTelegramChat(
        apiKey = config.telegram.apiKey,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
        severClient = webServer,
        httpClient = httpClient,
    )
    val botUser = TelegramBotImpl(
        telegramChat,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
        EngStringResources(),
        dispatchers,
    )
    return TelegramButtonListenerJob(
        dispatchers,
        telegramChat,
        botUser,
    )
}

class TelegramButtonListenerJob(
    private val dispatchers: Dispatchers,
    private val telegramChat: TelegramChat,
    private val bot: TelegramBot,
) : Job {

    private val coroutineScope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        coroutineScope.launch {
            telegramChat
                .getEvents()
                .onEach {
                    when (val action = ButtonAction.parse(it.action.value)) {
                        is ButtonAction.DataBase -> bot.startRepetitionSessionFor(action.notionDbId)
                        is ButtonAction.Forgotten -> bot.makeForgotten(action.flashCardId)
                        is ButtonAction.Recalled -> bot.makeRecalled(action.flashCardId)
                        is ButtonAction.Unknown -> bot.deleteMessage(it.messageId)
                    }
                }.collect(TelegramMessage.Button.Callback::answer)
        }
    }
}