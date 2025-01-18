package integrations

import com.google.gson.Gson
import integrations.testdata.english.vocabulary.EnglishVocabularyDataBaseLocalDbFake
import integrations.testdata.telegram.TelegramCallbackData
import io.kotest.core.spec.style.BehaviorSpec
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.app.data.dictionary.constant.ConstantOnlineDictionary
import org.danceofvalkyries.app.data.notion.databases.restful.RestFulNotionDataBases
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.users.bot.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import utils.*
import utils.fakes.httpclient.HttpClientFake
import utils.fakes.telegram.TelegramChatFake
import utils.fakes.telegram.TelegramMessageFake

class ButtonsListenersTest : BehaviorSpec() {

    private lateinit var telegramButtonListenerApp: TelegramButtonListenerApp
    private lateinit var ktorWebServer: KtorWebServerFake
    private lateinit var httpClientFake: HttpClientFake
    private lateinit var sqlLiteNotionDataBasesFake: SqlLiteNotionDataBasesFake
    private lateinit var sentTelegramMessagesTypeFake: SentTelegramMessagesTypeFake
    private lateinit var englishVocabularyDataBaseLocalDbFake: EnglishVocabularyDataBaseLocalDbFake
    private lateinit var cambridgeDictionary: OnlineDictionary

    private lateinit var telegramChat: TelegramChatFake

    init {

        Given("Notification App") {
            beforeTest {
                englishVocabularyDataBaseLocalDbFake = EnglishVocabularyDataBaseLocalDbFake()
                sqlLiteNotionDataBasesFake = SqlLiteNotionDataBasesFake(
                    listOf(englishVocabularyDataBaseLocalDbFake)
                )

                ktorWebServer = KtorWebServerFake(Gson())
                cambridgeDictionary = ConstantOnlineDictionary("https://dictionary.cambridge.org/dictionary/english/encounter")

                httpClientFake = HttpClientFake()
                telegramChat = TelegramChatFake()
                sentTelegramMessagesTypeFake = SentTelegramMessagesTypeFake()


                val restfulNotionDataBases = RestFulNotionDataBases(
                    desiredDbIds = listOf(TestData.Notion.EnglishVocabulary.DATA_BASE_ID),
                    apiKey = TestData.Notion.NOTION_API_KEY,
                    httpClient = httpClientFake,
                    gson = Gson()
                )
                val humanUser = TelegramHumanUserImpl(
                    sqlLiteNotionDataBasesFake,
                    restfulNotionDataBases,
                )
                val botUser = TelegramBotUserImpl(
                    telegramChat,
                    sqlLiteNotionDataBasesFake,
                    restfulNotionDataBases,
                    sentTelegramMessagesTypeFake,
                    OnlineDictionariesFake(listOf(cambridgeDictionary))
                )
                telegramButtonListenerApp = TelegramButtonListenerApp(
                    DispatchersFake(),
                    SpaceRepetitionSession(humanUser, botUser),
                    telegramChat
                )
            }
            When("User types a message in the chat") {
                lateinit var telegramMessage: TelegramMessage

                beforeTest {
                    telegramMessage = telegramChat.sendTextMessage(text = "Q", nestedButtons = emptyList())
                    telegramChat.userSendsCallback(
                        TelegramCallbackData(
                            id = "2",
                            messageId = telegramMessage.id,
                            action = TelegramMessage.Button.Action.Text("Q")
                        )
                    )
                }

                Then("Should delete that message from Telegram") {
                    telegramButtonListenerApp.run()
                    telegramChat.assertThat().wasDeleted(telegramMessage)
                }
            }

            When("Notification Message is shown") {
                lateinit var notificationMessage: TelegramMessage

                val wineFlashCard = TelegramMessageFake.createEnglishVocabularyFlashCard(
                    messageId = 2,
                    text = englishVocabularyDataBaseLocalDbFake.wine.name,
                    example = englishVocabularyDataBaseLocalDbFake.wine.example!!,
                    answer = englishVocabularyDataBaseLocalDbFake.wine.explanation!!,
                    flashCardId = englishVocabularyDataBaseLocalDbFake.wine.id,
                    dictionaryUrl = cambridgeDictionary.getUrlFor(englishVocabularyDataBaseLocalDbFake.wine.name),
                    imageUrl = englishVocabularyDataBaseLocalDbFake.wine.coverUrl
                )

                val dota2FlashCard = TelegramMessageFake.createEnglishVocabularyFlashCard(
                    messageId = 3,
                    text = englishVocabularyDataBaseLocalDbFake.dota2.name,
                    example = englishVocabularyDataBaseLocalDbFake.dota2.example!!,
                    answer = englishVocabularyDataBaseLocalDbFake.dota2.explanation!!,
                    flashCardId = englishVocabularyDataBaseLocalDbFake.dota2.id,
                    dictionaryUrl = cambridgeDictionary.getUrlFor(englishVocabularyDataBaseLocalDbFake.dota2.name),
                    imageUrl = englishVocabularyDataBaseLocalDbFake.dota2.coverUrl
                )

                beforeTest {
                    val message = TelegramMessageFake.createTelegramNotification(
                        messageId = -1,
                        numberToRevise = 2,
                        tableName = englishVocabularyDataBaseLocalDbFake.name,
                        dbId = englishVocabularyDataBaseLocalDbFake.id
                    )
                    notificationMessage = telegramChat.sendTextMessage(
                        text = message.text,
                        nestedButtons = message.nestedButtons
                    )
                    sentTelegramMessagesTypeFake.add(
                        id = notificationMessage.id,
                        type = "NOTIFICATION"
                    )
                    telegramButtonListenerApp.run()
                }

                And("User taps on English Vocabulary buttons") {
                    beforeTest {
                        telegramChat.userSendsCallback(
                            TelegramCallbackData(
                                id = "callback_id",
                                action = TelegramMessage.Button.Action.CallBackData("dbId=${englishVocabularyDataBaseLocalDbFake.id}"),
                                messageId = notificationMessage.id
                            )
                        )
                    }

                    Then("Wine Flash Card should be shown") {
                        telegramChat
                            .assertThat()
                            .isInChat(wineFlashCard)
                    }

                    Then("Notification Message should remain in Chat") {
                        telegramChat.assertThat()
                            .isInChat(notificationMessage)
                    }

                    And("User recalls Wine FlashCard") {
                        beforeTest {
                            telegramChat.userSendsCallback(
                                TelegramCallbackData(
                                    id = "callback_id",
                                    action = TelegramMessage.Button.Action.CallBackData("recalledFlashCardId=${englishVocabularyDataBaseLocalDbFake.wine.id}"),
                                    messageId = wineFlashCard.id
                                )
                            )
                        }

                        Then("Should Send Dota 2 Flash Card") {
                            telegramChat.assertThat()
                                .isInChat(dota2FlashCard)
                        }

                        Then("Should remove Wine Flash Card From Telegram") {
                            telegramChat.assertThat()
                                .wasDeleted(wineFlashCard)
                        }

                        Then("Should decrease counter on English Vocabulary Button") {
                            val editedNotificationMessage = TelegramMessageFake.createTelegramNotification(
                                messageId = notificationMessage.id,
                                numberToRevise = 1,
                                tableName = englishVocabularyDataBaseLocalDbFake.name,
                                dbId = englishVocabularyDataBaseLocalDbFake.id
                            )
                            telegramChat.assertThat()
                                .textMessageWasEdited(
                                    from = notificationMessage,
                                    to = editedNotificationMessage
                                )
                        }

                        Then("Notification Message should remain in Chat") {
                            telegramChat.assertThat()
                                .isInChat(notificationMessage)
                        }

                        Then("Notification Message should remain in Sent Messages DB") {
                            sentTelegramMessagesTypeFake.assertThat()
                                .presents(notificationMessage.id, "NOTIFICATION")
                        }
                    }
                }
            }
        }
    }

    private fun createApp(
        telegramChatFake: TelegramChatFake,
    ): App {
        val sqlLiteNotionDataBasesFake = SqlLiteNotionDataBasesFake(
            listOf(englishVocabularyDataBaseLocalDbFake)
        )

        val ktorWebServer = KtorWebServerFake(Gson())
        val cambridgeDictionary = ConstantOnlineDictionary("https://dictionary.cambridge.org/dictionary/english/encounter")

        val httpClientFake = HttpClientFake()
        val sentTelegramMessagesTypeFake = SentTelegramMessagesTypeFake()


        val restfulNotionDataBases = RestFulNotionDataBases(
            desiredDbIds = listOf(TestData.Notion.EnglishVocabulary.DATA_BASE_ID),
            apiKey = TestData.Notion.NOTION_API_KEY,
            httpClient = httpClientFake,
            gson = Gson()
        )
        val humanUser = TelegramHumanUserImpl(
            sqlLiteNotionDataBasesFake,
            restfulNotionDataBases,
        )
        val botUser = TelegramBotUserImpl(
            telegramChatFake,
            sqlLiteNotionDataBasesFake,
            restfulNotionDataBases,
            sentTelegramMessagesTypeFake,
            OnlineDictionariesFake(listOf(cambridgeDictionary))
        )
        return TelegramButtonListenerApp(
            DispatchersFake(),
            SpaceRepetitionSession(humanUser, botUser),
            telegramChatFake
        )
    }
}