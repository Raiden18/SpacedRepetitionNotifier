package integrations

import com.google.gson.Gson
import integrations.TestData.Notion.GreekLetterAndSounds.GREEK_LETTERS_AND_SOUNDS
import integrations.TestData.Notion.GreekLetterAndSounds.GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
import integrations.TestData.Notion.GreekLetterAndSounds.greekLettersAndSoundsTable
import integrations.TestData.Notion.GreekLetterAndSounds.greekSound1
import integrations.TestData.Notion.GreekLetterAndSounds.greekSound2
import integrations.TestData.Notion.NOTION_API_KEY
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import utils.*
import utils.fakes.httpclient.NewHttpClientFake

class NotifierAppTest : BehaviorSpec() {

    private lateinit var notifierApp: NotifierApp
    private lateinit var httpClient: NewHttpClientFake
    private lateinit var sentTelegramMessagesType: SentTelegramMessagesTypeFake
    private lateinit var sqlLiteNotionDataBases: SqlLiteNotionDataBasesFake


    init {
        beforeTest {
            httpClient = NewHttpClientFake()
            sentTelegramMessagesType = SentTelegramMessagesTypeFake()
            val telegramChat = RestfulTelegramChat(
                TestData.TELEGRAM_API_KEY,
                Gson(),
                TestData.CHAT_ID,
                KtorWebServerFake(Gson()),
                httpClient,
            )
            sqlLiteNotionDataBases = SqlLiteNotionDataBasesFake()
            val onlineDictionaries = OnlineDictionariesFake(emptyList())
            val restfulNotionDataBases = RestFulNotionDataBases(
                desiredDbIds = listOf(GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID),
                apiKey = NOTION_API_KEY,
                httpClient = httpClient,
                gson = Gson()
            )

            val telegramBot = TelegramBotUserImpl(
                telegramChat,
                sqlLiteNotionDataBases,
                restfulNotionDataBases,
                sentTelegramMessagesType,
                onlineDictionaries,
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
                    httpClient.mockNewResponse()
                        .url("https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID")
                        .get()
                        .responseWith(greekLettersAndSoundsTable)
                        .build()
                }

                When("Number of Flash Cards is more than threshold") {
                    beforeTest {
                        httpClient.mockNewResponse()
                            .url("https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query")
                            .post(TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest())
                            .responseWith(
                                TestData.Notion.GreekLetterAndSounds.pagesResponse(
                                    listOf(
                                        greekSound1,
                                        greekSound2
                                    )
                                )
                            ).build()
                    }

                    Then("Should send notification to Telegram") {
                        val expectedNotificationMessage =
                            TestData.Telegram.SendMessage.notificationRequestWithOneButton(
                                text = """You have 2 flashcards to revise 🧠""",
                                buttonTitle = "$GREEK_LETTERS_AND_SOUNDS: 2",
                                notionDataBaseId = GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID
                            )
                        val notificationBodyResponse = TestData.Telegram.SendMessage.notificationResponseWithOneButton(messageId = 228)

                        httpClient.mockNewResponse()
                            .url(TestData.Telegram.Urls.getSendMessage())
                            .post(expectedNotificationMessage)
                            .responseWith(notificationBodyResponse)
                            .build()

                        notifierApp.run()

                        httpClient.assertThat()
                            .post(
                                url = TestData.Telegram.Urls.getSendMessage(),
                                body = expectedNotificationMessage,
                            ).wasSent()
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
                    httpClient.mockNewResponse()
                        .url("https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID")
                        .get()
                        .responseWith(greekLettersAndSoundsTable)
                        .build()

                }

                When("Number of Flash Cards is more than threshold") {
                    beforeTest {
                        httpClient.mockNewResponse()
                            .url("https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query")
                            .post(TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest())
                            .responseWith(
                                TestData.Notion.GreekLetterAndSounds.pagesResponse(
                                    listOf(
                                        greekSound1,
                                        greekSound2
                                    )
                                )
                            ).build()

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
                        httpClient.mockNewResponse()
                            .url(TestData.Telegram.Urls.getSendMessage())
                            .post(expectedNotificationMessage)
                            .responseWith(notificationBodyResponse)
                            .build()

                        httpClient.mockNewResponse()
                            .url(TestData.Telegram.Urls.getDeleteMessageUrl(sentNotificationMessageId))
                            .get()
                            .responseWith("{}")
                            .build()

                        notifierApp.run()
                    }

                    Then("Should send NEW notification to Telegram") {
                        httpClient.assertThat()
                            .post(
                                url = TestData.Telegram.Urls.getSendMessage(),
                                body = expectedNotificationMessage,
                            ).wasSent()
                    }

                    Then("Should Delete OLD notification From DB") {
                        val containsOldMessage = sentTelegramMessagesType.iterate().toList()
                            .firstOrNull { it.id == sentNotificationMessageId }
                        containsOldMessage shouldBe null
                    }

                    Then("Should Delete OLD notification from Telegram") {
                        httpClient.assertThat()
                            .get(TestData.Telegram.Urls.getDeleteMessageUrl(sentNotificationMessageId))
                            .wasSent()
                    }
                }

                When("Number of Flash Cards is less than threshold") {

                    beforeTest {
                        httpClient.mockNewResponse()
                            .url("https://api.notion.com/v1/databases/$GREEK_SOUNDS_AND_LETTERS_NOTION_DATA_BASE_ID/query")
                            .post(TestData.Notion.GreekLetterAndSounds.unrevisedCardBodyRequest())
                            .responseWith(TestData.Notion.GreekLetterAndSounds.pagesResponse(listOf(greekSound1)))
                            .build()
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
                        httpClient.mockNewResponse()
                            .url(TestData.Telegram.Urls.getEditMessage())
                            .post(expectedNotificationMessage)
                            .responseWith(notificationBodyResponse)
                            .build()
                        notifierApp.run()
                    }

                    Then("Then should edit PREVIOUS notification to DONE") {
                        httpClient.assertThat()
                            .post(
                                url = TestData.Telegram.Urls.getEditMessage(),
                                body = expectedNotificationMessage,
                            ).wasSent()
                    }
                }
            }
        }
    }
}