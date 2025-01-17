package integrations

import com.google.gson.Gson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import org.danceofvalkyries.utils.rest.jsonObject
import utils.*

class ButtonsListenersTest : BehaviorSpec() {

    private lateinit var spaceRepetitionSession: SpaceRepetitionSession
    private lateinit var telegramButtonListenerApp: TelegramButtonListenerApp
    private lateinit var ktorWebServer: KtorWebServerFake
    private lateinit var httpClientFake: HttpClientFake
    private lateinit var notionDataBasesFake: NotionDataBasesFake

    init {

        beforeTest {
            val gson = Gson()
            ktorWebServer = KtorWebServerFake(gson)
            httpClientFake = HttpClientFake()
            notionDataBasesFake = NotionDataBasesFake()
            val telegramChat = RestfulTelegramChat(
                apiKey = TestData.TELEGRAM_API_KEY,
                OkHttpClient(),
                Gson(),
                TestData.CHAT_ID,
                ktorWebServer,
                httpClientFake
            )
            val restfulNotionDataBases = NotionDataBasesFake()
            val telegramMessagesType = SentTelegramMessagesTypeFake()
            val humanUser = TelegramHumanUserImpl(
                notionDataBasesFake,
                restfulNotionDataBases,
            )
            val onlineDictionariesFake = OnlineDictionariesFake(
                emptyList()
            )
            val botUser = TelegramBotUserImpl(
                telegramChat,
                notionDataBasesFake,
                telegramMessagesType,
                onlineDictionariesFake
            )
            val spaceRepetitionSession = SpaceRepetitionSession(humanUser, botUser)
            telegramButtonListenerApp = TelegramButtonListenerApp(
                DispatchersFake(),
                spaceRepetitionSession,
                telegramChat
            )
        }

        Given("Listener App") {

            val messageId = 2868L

            When("User types a message in chat") {
                beforeTest {
                    val responseFromTelegram = jsonObject {
                        "update_id" to 2
                        "message" to jsonObject {
                            "message_id" to messageId
                            "from" to jsonObject {
                                "id" to 3
                                "is_bot" to false
                                "first_name" to "Paul"
                                "username" to "DanceOfValkyries"
                                "language_code" to "en"
                            }
                            "chat" to jsonObject {
                                "id" to 4
                                "first_name" to "Paul"
                                "username" to "DanceOfValkyries"
                                "type" to "private"
                            }
                            "date" to 1737094191
                            "text" to "Q"
                        }
                    }

                    ktorWebServer.send(responseFromTelegram)
                }

                Then("Should delete that message on Telegram").config() {
                    telegramButtonListenerApp.run()
                    httpClientFake.getUrlRequests shouldContain TestData.Telegram.Urls.getDeleteMessageUrl(messageId)
                }
            }
        }

        Given("Flash Cards from English vocabulary") {
            val englishVocabularyId = "488338833838"
            val word1 = NotionPageFlashCardFake(
                id = "00001",
                coverUrl = "https://images.ctfassets.net/8x8155mjsjdj/1af9dvSFEPGCzaKvs8XQ5O/a7d4adc8f9573183394ef2853afeb0b6/Copy_of_Red_Wine_Blog_Post_Header.png",
                notionDbID = englishVocabularyId,
                name = "Wine",
                example = "I do not drink wine at all",
                explanation = "Alcoholic beverage made of grapes",
                knowLevels = mapOf(
                    1 to true,
                    2 to false,
                    3 to false,
                    4 to false,
                    5 to false,
                    6 to false,
                    7 to false,
                    8 to false,
                    9 to false,
                    10 to false,
                    11 to false,
                    12 to false,
                    13 to false,
                )
            )
            val word2 = NotionPageFlashCardFake(
                id = "00002",
                coverUrl = "https://chaumette.com/wp-content/uploads/2020/03/wine-gone-bad-900x675.jpg",
                notionDbID = englishVocabularyId,
                name = "Dota 2",
                example = "Dota 2 is the best game in the world.",
                explanation = "I love Dota 2",
                knowLevels = mapOf(
                    1 to true,
                    2 to true,
                    3 to false,
                    4 to false,
                    5 to false,
                    6 to false,
                    7 to false,
                    8 to false,
                    9 to false,
                    10 to false,
                    11 to false,
                    12 to false,
                    13 to false,
                )
            )

            beforeTest {
                val notionDataBase = NotionDataBaseFake(
                    id = englishVocabularyId,
                    name = "English Vocabulary",
                    pages = mutableListOf(word1, word2)
                )
                notionDataBasesFake.add(notionDataBase)
            }
            When("User taps on Greek Letters and Sounds Button") {

                beforeTest {
                    ktorWebServer.send()
                }

                Then("Flash Card Message should be sent") {

                }
            }
        }
    }
}