package integrations

import com.google.gson.Gson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import utils.*
import utils.HttpClientFake.PostRequest

class NotifierAppTest : BehaviorSpec() {
    private val NOTION_API_KEY = "NOTION_API_KEY"

    private val GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID = "greek_sounds_and_letters_db_id"

    private val TELEGRAM_SEND_MESSAGE_URL = "https://api.telegram.org/bot${TestData.TELEGRAM_API_KEY}/sendMessage"
    private val TELEGRAM_END_MESSAGE_URL = "https://api.telegram.org/bot${TestData.TELEGRAM_API_KEY}/editMessageText"
    private val GREEK_LETTERS_AND_SOUNDS = "Greek Letters and Sounds"

    private val greekSound1 = TestData.Notion.GreekLetterAndSounds.pageResponse(
        id = "greek_letter_id_1",
        dataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID,
        name = "Αα",
        explanation = "a as in raft",
    )
    private val greekSound2 = TestData.Notion.GreekLetterAndSounds.pageResponse(
        id = "greek_letter_id_2",
        dataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID,
        name = "Ββ",
        explanation = "v as in vet",
    )

    private val greekLettersAndSoundsTable = TestData.Notion.GreekLetterAndSounds.dataBaseResponse(
        id = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID,
        name = GREEK_LETTERS_AND_SOUNDS,
    )

    private lateinit var notifierApp: NotifierApp
    private lateinit var httpClient: HttpClientFake
    private lateinit var sentTelegramMessagesType: SentTelegramMessagesTypeFake


    init {
        beforeTest {
            httpClient = HttpClientFake()
            sentTelegramMessagesType = SentTelegramMessagesTypeFake()
            val telegramChat = RestfulTelegramChat(
                TestData.TELEGRAM_API_KEY,
                OkHttpClient(),
                Gson(),
                TestData.CHAT_ID,
                KtorWebServerFake(Gson()),
                httpClient,
            )
            val sqlLiteNotionDataBases = NotionDataBasesFake()
            val onlineDictionaries = OnlineDictionariesFake(emptyList())

            val telegramBot = TelegramBotUserImpl(
                telegramChat,
                sqlLiteNotionDataBases,
                sentTelegramMessagesType,
                onlineDictionaries,
            )
            val restfulNotionDataBases = RestFulNotionDataBases(
                desiredDbIds = listOf(GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID),
                apiKey = NOTION_API_KEY,
                httpClient = httpClient,
                okHttpClient = OkHttpClient(),
                gson = Gson()
            )

            notifierApp = NotifierApp(
                DispatchersFake(),
                flashCardsThreshold = 1,
                restfulNotionDataBases,
                sqlLiteNotionDataBases,
                telegramBot,
            )
        }

        Given("No notification Messages were sent to Telegram") {
            beforeTest {
                sentTelegramMessagesType.clear()
            }

            And("Greek Letters And Sounds Notion Table") {
                beforeTest {
                    httpClient.mockGetResponse(
                        url = "https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID",
                        response = greekLettersAndSoundsTable
                    )
                }

                When("Number of Flash Cards is more than threshold") {
                    beforeTest {
                        httpClient.mockPostResponse(
                            url = "https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query",
                            body = TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest(),
                            response = TestData.Notion.GreekLetterAndSounds.pagesResponse(
                                listOf(
                                    greekSound1,
                                    greekSound2
                                )
                            )
                        )
                    }

                    Then("Should send notification to Telegram") {

                        val expectedNotificationMessage = TestData.Telegram.SendMessage.notificationRequestWithOneButton(
                            text = """You have 2 flashcards to revise 🧠""",
                            buttonTitle = "$GREEK_LETTERS_AND_SOUNDS: 2",
                            notionDataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
                        )
                        val notificationBodyResponse = TestData.Telegram.SendMessage.notificationResponseWithOneButton(messageId = 228)

                        httpClient.mockPostResponse(
                            url = TELEGRAM_SEND_MESSAGE_URL,
                            body = expectedNotificationMessage,
                            response = notificationBodyResponse
                        )

                        notifierApp.run()

                        httpClient.postRequests shouldContain PostRequest(
                            url = TELEGRAM_SEND_MESSAGE_URL,
                            body = expectedNotificationMessage,
                        )
                    }
                }

            }
        }

        Given("Notification Message was already sent") {
            val sentNotificationMessageId = 228L
            beforeTest {
                sentTelegramMessagesType.add(
                    id = sentNotificationMessageId,
                    type = "NOTIFICATION"
                )
            }

            And("Greek Letters And Sounds Notion Table") {

                beforeTest {
                    httpClient.mockGetResponse(
                        url = "https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID",
                        response = greekLettersAndSoundsTable
                    )
                }

                When("Number of Flash Cards is more than threshold") {
                    beforeTest {
                        httpClient.mockPostResponse(
                            url = "https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query",
                            body = TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest(),
                            response = TestData.Notion.GreekLetterAndSounds.pagesResponse(
                                listOf(
                                    greekSound1,
                                    greekSound2
                                )
                            )
                        )
                    }

                    And("App is being run") {

                        val expectedNotificationMessage = TestData.Telegram.SendMessage.notificationRequestWithOneButton(
                            text = """You have 2 flashcards to revise 🧠""",
                            buttonTitle = "$GREEK_LETTERS_AND_SOUNDS: 2",
                            notionDataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
                        )
                        val notificationBodyResponse = TestData.Telegram.SendMessage.notificationResponseWithOneButton(messageId = 322)

                        beforeTest {
                            httpClient.mockPostResponse(
                                url = TELEGRAM_SEND_MESSAGE_URL,
                                body = expectedNotificationMessage,
                                response = notificationBodyResponse
                            )

                            notifierApp.run()
                        }

                        Then("Should send NEW notification to Telegram") {
                            httpClient.postRequests shouldContain PostRequest(
                                url = TELEGRAM_SEND_MESSAGE_URL,
                                body = expectedNotificationMessage,
                            )
                        }

                        Then("Should Delete OLD notification From DB") {
                            val containsOldMessage = sentTelegramMessagesType.iterate().toList().firstOrNull { it.id == sentNotificationMessageId }
                            containsOldMessage shouldBe null
                        }

                        Then("Should Delete OLD notification from TELEGRAM") {
                            httpClient.getUrlRequests shouldContain TestData.Telegram.Urls.getDeleteMessageUrl(sentNotificationMessageId)
                        }
                    }
                }

                When("Number of Flash Cards is less than threshold") {
                    beforeTest {
                        httpClient.mockPostResponse(
                            url = "https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query",
                            body = TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest(),
                            response = TestData.Notion.GreekLetterAndSounds.pagesResponse(listOf(greekSound1))
                        )
                    }

                    Then("Then should edit OLD notification to DONE") {
                        val text = """Good Job! 😎 Everything is revised! ✅"""
                        val expectedNotificationMessage = TestData.Telegram.SendMessage.notificationDoneRequest(
                            messageId = sentNotificationMessageId,
                            text = text
                        )
                        val notificationBodyResponse = TestData.Telegram.SendMessage.notificationDoneResponse(
                            messageId = sentNotificationMessageId,
                            text = text,
                        )

                        httpClient.mockPostResponse(
                            url = TELEGRAM_END_MESSAGE_URL,
                            body = expectedNotificationMessage,
                            response = notificationBodyResponse
                        )

                        notifierApp.run()

                        httpClient.postRequests shouldContain PostRequest(
                            url = TELEGRAM_SEND_MESSAGE_URL,
                            body = expectedNotificationMessage,
                        )
                    }
                }
            }
        }
    }
}