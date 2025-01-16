package org.danceofvalkyries.app.apps.buttonslistener

import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardsController
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.HumanUser
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
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
        gson = Gson(),
        httpClient = environment.httpClient,
        apiKey = config.telegram.apiKey
    )
    return TelegramButtonListenerApp(
        dispatchers,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
        botUser,
        humanUser
    )
}

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val botUser: TelegramBotUser,
    private val humanUser: HumanUser,
) : App {

    override suspend fun run() {
        val spaceRepetitionSession = SpaceRepetitionSessionImpl(
            notionDataBases = sqlLiteNotionDataBases,
            restfullNotionDataBase = restfulNotionDataBases,
        )
        val flashCardsController = FlashCardsController(
            spaceRepetitionSession,
            botUser,
        )

        humanUser.getActions()
            .onEach {
                when (val action = ButtonAction.parse(it.action.value)) {
                    is ButtonAction.DataBase -> flashCardsController.onDataBaseClicked(action.notionDbId)
                    is ButtonAction.Forgotten -> flashCardsController.onForgottenClicked(action.flashCardId)
                    is ButtonAction.Recalled -> flashCardsController.onRecalledClicked(action.flashCardId)
                    is ButtonAction.Unknown -> Unit
                }
            }.onEach { it.answer() }
            .collectLatest(::println)
    }
}