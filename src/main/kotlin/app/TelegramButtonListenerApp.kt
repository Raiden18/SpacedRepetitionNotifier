package org.danceofvalkyries.app

import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDaoImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDaoImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTableImpl
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageDaoImpl
import org.danceofvalkyries.app.domain.FlashCardsController
import org.danceofvalkyries.app.domain.TelegramChatFlashCardView
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.app.domain.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.app.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApiImpl
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApiImpl
import org.danceofvalkyries.notion.impl.restapi.NotionApiImpl
import org.danceofvalkyries.telegram.impl.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApiImpl
import org.danceofvalkyries.telegram.impl.client.TelegramChatRestApiImpl
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
            TelegramChatRestApiImpl(
                client = environment.httpClient,
                gson = createGson(),
                apiKey = config.telegram.apiKey,
            ),
            config.telegram.chatId
        )

        val notionApi = NotionApiImpl(
            gson = createGson(),
            client = environment.httpClient,
            apiKey = config.notion.apiKey,
        )
        val notionDatabaseDataBaseTable = NotionDatabaseDataBaseTableImpl(NotionDataBaseDaoImpl(dbConnection))
        val notionDataBaseApi = NotionDataBaseApiImpl(notionApi)

        val notionPageFlashCardDao = NotionPageFlashCardDaoImpl(dbConnection)
        val notionPageFlashCardDataBaseTable = NotionPageFlashCardDataBaseTableImpl(notionPageFlashCardDao)

        val flashCardNotionPageApi = FlashCardNotionPageApiImpl(notionApi)

        val telegramMessageDao = TelegramMessageDaoImpl(dbConnection)
        val telegramMessagesDataBaseTable = TelegramMessagesDataBaseTableImpl(telegramMessageDao)

        val getOnlineDictionariesForFlashCard = GetOnlineDictionariesForFlashCard(config.notion.observedDatabases)

        val sendMessageToTelegramChat = SendMessageToTelegramChat(telegramApi)

        val telegramChatFlashCardView = TelegramChatFlashCardView(
            sendMessageToTelegramChat,
            telegramApi,
            getOnlineDictionariesForFlashCard,
        )

        val spaceRepetitionSession = SpaceRepetitionSessionImpl(
            notionPageFlashCardDataBaseTable,
            flashCardNotionPageApi,
        )

        val flashCardsController = FlashCardsController(
            spaceRepetitionSession,
            telegramChatFlashCardView
        )
        telegramApi.getUpdates()
            .onEach {
                val messageId = it.telegramUpdateCallbackQuery.message.id
                when (val action = ButtonAction.parse(it.callback.value)) {
                    is ButtonAction.DataBase -> flashCardsController.onDataBaseClicked(action.notionDbId)
                    is ButtonAction.Forgotten -> flashCardsController.onForgottenClicked(action.flashCardId, messageId)
                    is ButtonAction.Recalled -> flashCardsController.onRecalledClicked(action.flashCardId, messageId)
                }
            }.collectLatest(::println)
    }

    private fun createGson(): Gson {
        return Gson()
    }
}