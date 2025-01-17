package integrations.interactions

import io.kotest.core.spec.style.BehaviorSpec
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import utils.NotionDataBasesFake
import utils.OnlineDictionariesFake
import utils.TelegramMessagesTypeFake

class NotifierAppTest : BehaviorSpec() {

    private lateinit var notifierApp: NotifierApp
    private val API_KEY = "API_KEY"

    init {
        beforeTest {
            val telegramChat = RestfulTelegramChat()
            val notionDataBases = NotionDataBasesFake()
            val telegramMessagesType = TelegramMessagesTypeFake()
            val onlineDictionaries = OnlineDictionariesFake(emptyList())

            val telegramBot = TelegramBotUserImpl(
                telegramChat,
                notionDataBases,
                telegramMessagesType,
                onlineDictionaries,
            ),
            val restfulNotionDataBases = NotionDataBasesFake()
            val sqlLiteNotionDataBases = NotionDataBasesFake()
            notifierApp = NotifierApp(
                flashCardsThreshold = 10,
                restfulNotionDataBases,
                sqlLiteNotionDataBases,
                telegramBot,
            )
        }
    }
}