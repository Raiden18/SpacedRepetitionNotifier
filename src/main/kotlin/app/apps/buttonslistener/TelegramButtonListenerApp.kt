package org.danceofvalkyries.app.apps.buttonslistener

import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardsController
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.app.apps.buttonslistener.presentation.view.TelegramNotificationView
import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.app.data.telegram.message_types.sqlite.SqlLiteTelegramMessagesType
import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.data.telegram_and_notion.sqlite.SqlLiteSentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.data.users.bot.TelegramBotUser
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
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
    val telegramApi = TelegramChatApiImpl(
        gson = Gson(),
    )
    val onlineDictionaries = ConfigOnlineDictionaries(config.notion.observedDatabases)
    val telegramChat = RestfulTelegramChat(
        apiKey = config.telegram.apiKey,
        client = environment.httpClient,
        gson = Gson(),
        chatId = environment.config.telegram.chatId,
    )
    val botUser = TelegramBotUser(
        telegramChat,
        sqlLiteNotionDataBases,
        sqlLiteTelegramMessages,
        onlineDictionaries,
    )
    return TelegramButtonListenerApp(
        dispatchers,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
        sqlLiteTelegramMessages,
        telegramApi,
        telegramChat,
        botUser
    )
}

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val telegramMessagesType: TelegramMessagesType,
    private val telegramChatApi: TelegramChatApi,
    private val telegramChat: TelegramChat,
    private val botUser: TelegramBotUser,
) : App {

    override suspend fun run() {
        val telegramNotificationView = TelegramNotificationView(
            sqlLiteNotionDataBases,
            telegramChat,
            telegramMessagesType,
        )
        val spaceRepetitionSession = SpaceRepetitionSessionImpl(
            notionDataBases = sqlLiteNotionDataBases,
            restfullNotionDataBase = restfulNotionDataBases,
        )
        val flashCardsController = FlashCardsController(
            spaceRepetitionSession,
            telegramNotificationView,
            botUser,
        )
        telegramChatApi.getUpdates()
            .onEach {
                when (val action = ButtonAction.parse(it.callback.value)) {
                    is ButtonAction.DataBase -> flashCardsController.onDataBaseClicked(action.notionDbId)
                    is ButtonAction.Forgotten -> flashCardsController.onForgottenClicked(action.flashCardId)
                    is ButtonAction.Recalled -> flashCardsController.onRecalledClicked(action.flashCardId)
                }
            }.onEach { telegramChat.answerCallBack(it.telegramUpdateCallbackQuery.id) }
            .collectLatest(::println)
    }
}