package integrations

import com.google.gson.Gson
import integrations.TestData.Notion.GreekLetterAndSounds.GREEK_LETTERS_AND_SOUNDS
import integrations.TestData.Notion.GreekLetterAndSounds.GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
import integrations.TestData.Notion.GreekLetterAndSounds.greekLettersAndSoundsTable
import integrations.TestData.Notion.GreekLetterAndSounds.greekSound1
import integrations.TestData.Notion.GreekLetterAndSounds.greekSound2
import integrations.TestData.Notion.NOTION_API_KEY
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
    private lateinit var notifierApp: NotifierApp
    private lateinit var httpClient: HttpClientFake
    private lateinit var sentTelegramMessagesType: SentTelegramMessagesTypeFake
    private lateinit var sqlLiteNotionDataBases: NotionDataBasesFake


    init {
        beforeTest {
            httpClient = HttpClientFake()
            sentTelegramMessagesType = SentTelegramMessagesTypeFake()
            val telegramChat = RestfulTelegramChat(
                TestData.TELEGRAM_API_KEY,
                Gson(),
                TestData.CHAT_ID,
                KtorWebServerFake(Gson()),
                httpClient,
            )
            sqlLiteNotionDataBases = NotionDataBasesFake()
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
                flashCardsThreshold = 2,
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
                        val expectedNotificationMessage =
                            TestData.Telegram.SendMessage.notificationRequestWithOneButton(
                                text = """You have 2 flashcards to revise 🧠""",
                                buttonTitle = "$GREEK_LETTERS_AND_SOUNDS: 2",
                                notionDataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
                            )
                        val notificationBodyResponse =
                            TestData.Telegram.SendMessage.notificationResponseWithOneButton(messageId = 228)

                        httpClient.mockPostResponse(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = expectedNotificationMessage,
                            response = notificationBodyResponse
                        )

                        notifierApp.run()

                        httpClient.postRequests shouldContain PostRequest(
                            url = TestData.Telegram.Urls.getSendMessage(),
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

                    val expectedNotificationMessage =
                        TestData.Telegram.SendMessage.notificationRequestWithOneButton(
                            text = """You have 2 flashcards to revise 🧠""",
                            buttonTitle = "$GREEK_LETTERS_AND_SOUNDS: 2",
                            notionDataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
                        )
                    val notificationBodyResponse =
                        TestData.Telegram.SendMessage.notificationResponseWithOneButton(messageId = 322)

                    beforeTest {
                        httpClient.mockPostResponse(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = expectedNotificationMessage,
                            response = notificationBodyResponse
                        )

                        notifierApp.run()
                    }

                    Then("Should send NEW notification to Telegram") {
                        httpClient.postRequests shouldContain PostRequest(
                            url = TestData.Telegram.Urls.getSendMessage(),
                            body = expectedNotificationMessage,
                        )
                    }

                    Then("Should Delete OLD notification From DB") {
                        val containsOldMessage = sentTelegramMessagesType.iterate().toList()
                            .firstOrNull { it.id == sentNotificationMessageId }
                        containsOldMessage shouldBe null
                    }

                    Then("Should Delete OLD notification from Telegram") {
                        httpClient.getUrlRequests shouldContain TestData.Telegram.Urls.getDeleteMessageUrl(
                            sentNotificationMessageId
                        )
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

                    val text = """Good Job! 😎 Everything is revised! ✅"""
                    val expectedNotificationMessage = TestData.Telegram.SendMessage.notificationDoneRequest(
                        messageId = sentNotificationMessageId,
                        text = text
                    )
                    val notificationBodyResponse = TestData.Telegram.SendMessage.notificationDoneResponse(
                        messageId = sentNotificationMessageId,
                        text = text,
                    )

                    beforeTest {
                        httpClient.mockPostResponse(
                            url = TestData.Telegram.Urls.getEditMessage(),
                            body = expectedNotificationMessage,
                            response = notificationBodyResponse
                        )
                        notifierApp.run()
                    }

                    Then("Then should edit PREVIOUS notification to DONE") {
                        httpClient.postRequests shouldContain PostRequest(
                            url = TestData.Telegram.Urls.getEditMessage(),
                            body = expectedNotificationMessage,
                        )
                    }
                }
            }
        }
    }
}