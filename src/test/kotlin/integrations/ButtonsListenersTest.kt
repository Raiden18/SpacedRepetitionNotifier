package integrations

import com.google.gson.Gson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.SpaceRepetitionSession
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import org.danceofvalkyries.utils.rest.jsonObject
import utils.*
import utils.fakes.httpclient.HttpClientFake
import utils.fakes.httpclient.HttpClientFake.PostRequest

class ButtonsListenersTest : BehaviorSpec() {

    private lateinit var telegramButtonListenerApp: TelegramButtonListenerApp
    private lateinit var ktorWebServer: KtorWebServerFake
    private lateinit var httpClientFake: HttpClientFake
    private lateinit var sqlLiteNotionDataBasesFake: SqlLiteNotionDataBasesFake
    private lateinit var sentTelegramMessagesTypeFake: SentTelegramMessagesTypeFake

    init {
        beforeTest {
            val gson = Gson()
            ktorWebServer = KtorWebServerFake(gson)
            httpClientFake = HttpClientFake()
            sqlLiteNotionDataBasesFake = SqlLiteNotionDataBasesFake()
            val telegramChat = RestfulTelegramChat(
                apiKey = TestData.TELEGRAM_API_KEY,
                Gson(),
                TestData.CHAT_ID,
                ktorWebServer,
                httpClientFake
            )
            val restfulNotionDataBases = RestFulNotionDataBases(
                desiredDbIds = listOf(TestData.Notion.EnglishVocabulary.DATA_BASE_ID),
                apiKey = TestData.Notion.NOTION_API_KEY,
                httpClient = httpClientFake,
                gson = Gson()
            )
            sentTelegramMessagesTypeFake = SentTelegramMessagesTypeFake()
            val humanUser = TelegramHumanUserImpl(
                sqlLiteNotionDataBasesFake,
                restfulNotionDataBases,
            )
            val onlineDictionariesFake = OnlineDictionariesFake(
                emptyList()
            )
            val botUser = TelegramBotUserImpl(
                telegramChat,
                sqlLiteNotionDataBasesFake,
                restfulNotionDataBases,
                sentTelegramMessagesTypeFake,
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

        Given("Flash Cards from English vocabulary are in Date Base") {
            val callbackQueryId = "3323123"
            val word1 = NotionPageFlashCardFake(
                id = "00001",
                coverUrl = null,
                notionDbID = TestData.Notion.EnglishVocabulary.DATA_BASE_ID,
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
                coverUrl = null,
                notionDbID = TestData.Notion.EnglishVocabulary.DATA_BASE_ID,
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

            val sendMessageBody = TestData.Telegram.SendMessage.flashCard(
                text = word1.name,
                example = word1.example!!,
                answer = word1.explanation!!,
                flashCardId = word1.id
            )

            beforeTest {
                val notionDataBase = NotionDataBaseFake(
                    id = TestData.Notion.EnglishVocabulary.DATA_BASE_ID,
                    name = "English Vocabulary",
                    pages = mutableListOf(word1, word2)
                )

                sqlLiteNotionDataBasesFake.add(notionDataBase)
            }

            When("User taps on English Vocabulary Button") {

                val word1FlashCardMessageId = 3123121L

                beforeTest {
                    ktorWebServer.send(
                        TestData.Telegram.Callback.notionDbButtonCallback(
                            callbackQueryId = callbackQueryId,
                            notionDbId = TestData.Notion.EnglishVocabulary.DATA_BASE_ID
                        )
                    )
                    httpClientFake.mockPostResponse(
                        url = TestData.Telegram.Urls.getSendMessage(),
                        body = sendMessageBody,
                        response = TestData.Telegram.SendMessage.notificationResponseWithOneButton(
                            messageId = word1FlashCardMessageId
                        )
                    )

                    telegramButtonListenerApp.run()
                }

                Then("Flash Card Message should be sent") {
                    runTest {
                        httpClientFake.postRequests shouldContain PostRequest(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = sendMessageBody
                        )
                    }
                }

                And("User clicks on Recall button") {

                    val word2FlashCardMessageBody = TestData.Telegram.SendMessage.flashCard(
                        text = word2.name,
                        example = word2.example!!,
                        answer = word2.explanation!!,
                        flashCardId = word2.id
                    )

                    val word2MessageId = 129387L

                    beforeTest {
                        httpClientFake.mockPostResponse(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = word2FlashCardMessageBody,
                            response = TestData.Telegram.SendMessage.notificationDoneResponse(
                                messageId = word2MessageId,
                                text = "Does not Matter"
                            )
                        )
                        ktorWebServer.send(
                            TestData.Telegram.Callback.recalledButtonClickedCallback(
                                callbackQueryId = callbackQueryId,
                                flashCardId = word1.id,
                                messageId = word1FlashCardMessageId
                            )
                        )
                    }

                    Then("Should remove that FlashCard from Telegram") {
                        httpClientFake.getUrlRequests shouldContain TestData.Telegram.Urls.getDeleteMessageUrl(
                            word1FlashCardMessageId
                        )
                    }

                    Then("Should remove That FlashCard from Local DB") {
                        val word1FlashCardFromDB = sqlLiteNotionDataBasesFake.iterate()
                            .flatMap { it.iterate() }
                            .firstOrNull { it.id == word1.id }
                        word1FlashCardFromDB shouldBe null
                    }

                    Then("Should sent NEW FlashCard to User on Telegram") {
                        httpClientFake.postRequests shouldContain PostRequest(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = word2FlashCardMessageBody,
                        )
                    }
                }
            }
        }
    }
}