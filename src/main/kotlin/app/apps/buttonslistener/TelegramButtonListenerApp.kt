package org.danceofvalkyries.app.apps.buttonslistener

import app.data.sqlite.notion.databases.SqlLiteNotionDataBases
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import notion.impl.client.NotionClientApiImpl
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardsController
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.app.apps.buttonslistener.presentation.view.TelegramChatFlashCardView
import org.danceofvalkyries.app.apps.buttonslistener.presentation.view.TelegramNotificationView
import org.danceofvalkyries.app.data.sqlite.notion.pages.flashcard.SqlLiteNotionPageFlashCards
import org.danceofvalkyries.app.data.sqlite.telegram.messages.SqlLiteTelegramMessages
import org.danceofvalkyries.app.data.sqlite.telegram_and_notion.SqlLiteSentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.impl.NotionApiImpl
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.utils.Dispatchers

//TODO: Fix: Make query for only one request
class TelegramButtonListenerApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    private val config: Config by lazy { environment.config }

    override suspend fun run() {
        val dbConnection = environment.dataBase.establishConnection()
        val telegramApi = TelegramChatApiImpl(
            client = environment.httpClient,
            gson = createGson(),
            apiKey = config.telegram.apiKey,
            chatId = config.telegram.chatId
        )

        val notionApi = NotionApiImpl(
            NotionClientApiImpl(
                gson = createGson(),
                client = environment.httpClient,
                apiKey = config.notion.apiKey,
            )
        )

        val notionDataBases = SqlLiteNotionDataBases(dbConnection)
        val notionPageFlashCards = SqlLiteNotionPageFlashCards(dbConnection)

        val getOnlineDictionariesForFlashCard = GetOnlineDictionariesForFlashCard(config.notion.observedDatabases)

        val sendMessageToTelegramChat = SendMessageToTelegramChat(telegramApi)
        val sentNotionPageFlashCardsToTelegram = SqlLiteSentNotionPageFlashCardsToTelegram(dbConnection)

        val telegramChatFlashCardView = TelegramChatFlashCardView(
            sendMessageToTelegramChat,
            telegramApi,
            getOnlineDictionariesForFlashCard,
            sentNotionPageFlashCardsToTelegram,
        )

        val telegramNotificationView = TelegramNotificationView(
            notionDataBases,
            telegramApi,
            SqlLiteTelegramMessages(dbConnection),
            notionPageFlashCards,
        )

        val spaceRepetitionSession = SpaceRepetitionSessionImpl(
            notionPageFlashCards,
            notionApi,
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

    private fun createGson(): Gson {
        return Gson()
    }
}