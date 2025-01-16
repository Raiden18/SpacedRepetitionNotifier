package org.danceofvalkyries.app.apps.buttonslistener

import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardsController
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.app.apps.buttonslistener.presentation.view.TelegramChatFlashCardView
import org.danceofvalkyries.app.apps.buttonslistener.presentation.view.TelegramNotificationView
import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.dictionary.config.ConfigOnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.notion.databases.sqlite.SqlLiteNotionDataBases
import org.danceofvalkyries.app.data.telegram.TelegramMessages
import org.danceofvalkyries.app.data.telegram.sqlite.SqlLiteTelegramMessages
import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.data.telegram_and_notion.sqlite.SqlLiteSentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat
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
    val sqlLiteTelegramMessages = SqlLiteTelegramMessages(dbConnection)
    val telegramApi = TelegramChatApiImpl(
        client = environment.httpClient,
        gson = Gson(),
        apiKey = config.telegram.apiKey,
        chatId = config.telegram.chatId
    )
    val onlineDictionaries = ConfigOnlineDictionaries(config.notion.observedDatabases)
    return TelegramButtonListenerApp(
        dispatchers,
        environment,
        sqlLiteNotionDataBases,
        restfulNotionDataBases,
        sentNotionPageFlashCardsToTelegram,
        sqlLiteTelegramMessages,
        telegramApi,
        onlineDictionaries
    )
}

class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
    private val sqlLiteNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val sqlLiteSentNotionPageFlashCardsToTelegram: SentNotionPageFlashCardsToTelegram,
    private val telegramMessages: TelegramMessages,
    private val telegramApi: TelegramChatApi,
    private val onlineDictionaries: OnlineDictionaries,
) : App {

    override suspend fun run() {
        val sendMessageToTelegramChat = SendMessageToTelegramChat(telegramApi)
        val telegramChatFlashCardView = TelegramChatFlashCardView(
            sendMessageToTelegramChat,
            telegramApi,
            onlineDictionaries,
            sqlLiteSentNotionPageFlashCardsToTelegram,
        )
        val telegramNotificationView = TelegramNotificationView(
            sqlLiteNotionDataBases,
            telegramApi,
            telegramMessages,
        )
        val spaceRepetitionSession = SpaceRepetitionSessionImpl(
            notionDataBases = sqlLiteNotionDataBases,
            restfullNotionDataBase = restfulNotionDataBases,
        )
        val flashCardsController = FlashCardsController(
            spaceRepetitionSession,
            telegramChatFlashCardView,
            telegramNotificationView,
        )
        telegramApi.getUpdates()
            .onEach {
                when (val action = ButtonAction.parse(it.callback.value)) {
                    is ButtonAction.DataBase -> flashCardsController.onDataBaseClicked(action.notionDbId)
                    is ButtonAction.Forgotten -> flashCardsController.onForgottenClicked(action.flashCardId)
                    is ButtonAction.Recalled -> flashCardsController.onRecalledClicked(action.flashCardId)
                }
            }.onEach { telegramApi.answerCallbackQuery(it.telegramUpdateCallbackQuery) }
            .collectLatest(::println)
    }
}