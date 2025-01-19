package job.telegram_listener

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.job.Job
import org.danceofvalkyries.job.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.job.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.job.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.job.data.telegram.bot.TelegramBot
import org.danceofvalkyries.job.data.telegram.bot.TelegramBotImpl
import org.danceofvalkyries.job.data.telegram.chat.TelegramChat
import org.danceofvalkyries.job.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.job.data.telegram.message.TelegramMessage.Button
import org.danceofvalkyries.job.data.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.job.data.telegram.message_types.sqlite.SqlLiteSentTelegramMessagesType
import org.danceofvalkyries.job.telegram_listener.ButtonAction
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
        TelegramTextTranslator(),
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
                }.collect(Button.Callback::answer)
        }
    }
}