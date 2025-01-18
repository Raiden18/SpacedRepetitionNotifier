package integrations

import integrations.testdata.english.vocabulary.EnglishVocabularyDataBaseFake
import integrations.testdata.telegram.TelegramCallbackData
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.app.data.dictionary.constant.ConstantOnlineDictionary
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.users.bot.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import utils.DispatchersFake
import utils.OnlineDictionariesFake
import utils.SentTelegramMessagesTypeFake
import utils.SqlLiteNotionDataBasesFake
import utils.fakes.httpclient.HttpClientFake
import utils.fakes.telegram.TelegramChatFake
import utils.fakes.telegram.TelegramMessageFake

class ButtonsListenersTest : BehaviorSpec() {

    private lateinit var telegramButtonListenerApp: TelegramButtonListenerApp
    private lateinit var httpClientFake: HttpClientFake
    private lateinit var sqlLiteNotionDataBasesFake: SqlLiteNotionDataBasesFake
    private lateinit var sentTelegramMessagesTypeFake: SentTelegramMessagesTypeFake

    private lateinit var englishVocabularyDataBaseFake: EnglishVocabularyDataBaseFake
    private lateinit var englishVocabularyDataBaseRestfulFake: EnglishVocabularyDataBaseFake


    private lateinit var cambridgeDictionary: OnlineDictionary

    private lateinit var telegramChat: TelegramChatFake

    init {

        Given("Notification App") {
            beforeTest {
                englishVocabularyDataBaseFake = EnglishVocabularyDataBaseFake()
                englishVocabularyDataBaseRestfulFake = EnglishVocabularyDataBaseFake()

                sqlLiteNotionDataBasesFake = SqlLiteNotionDataBasesFake(
                    listOf(englishVocabularyDataBaseFake)
                )
                cambridgeDictionary = ConstantOnlineDictionary("https://dictionary.cambridge.org/dictionary/english/encounter")

                httpClientFake = HttpClientFake()
                telegramChat = TelegramChatFake()
                sentTelegramMessagesTypeFake = SentTelegramMessagesTypeFake()


                val restfulNotionDataBases = SqlLiteNotionDataBasesFake(listOf(englishVocabularyDataBaseRestfulFake))

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
                    text = englishVocabularyDataBaseFake.wine.name,
                    example = englishVocabularyDataBaseFake.wine.example!!,
                    answer = englishVocabularyDataBaseFake.wine.explanation!!,
                    flashCardId = englishVocabularyDataBaseFake.wine.id,
                    dictionaryUrl = cambridgeDictionary.getUrlFor(englishVocabularyDataBaseFake.wine.name),
                    imageUrl = englishVocabularyDataBaseFake.wine.coverUrl
                )

                val dota2FlashCard = TelegramMessageFake.createEnglishVocabularyFlashCard(
                    messageId = 3,
                    text = englishVocabularyDataBaseFake.dota2.name,
                    example = englishVocabularyDataBaseFake.dota2.example!!,
                    answer = englishVocabularyDataBaseFake.dota2.explanation!!,
                    flashCardId = englishVocabularyDataBaseFake.dota2.id,
                    dictionaryUrl = cambridgeDictionary.getUrlFor(englishVocabularyDataBaseFake.dota2.name),
                    imageUrl = englishVocabularyDataBaseFake.dota2.coverUrl
                )

                beforeTest {
                    val message = TelegramMessageFake.createTelegramNotification(
                        messageId = -1,
                        numberToRevise = 2,
                        tableName = englishVocabularyDataBaseFake.name,
                        dbId = englishVocabularyDataBaseFake.id
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
                                action = TelegramMessage.Button.Action.CallBackData("dbId=${englishVocabularyDataBaseFake.id}"),
                                messageId = notificationMessage.id
                            )
                        )
                    }

                    shouldSendWineFlashCardToTelegramChat(wineFlashCard)
                    notificationMessageShouldRemainIntactInTelegramChat(notificationMessage)

                    And("User forgets Wine FlashCard") {
                        beforeTest {
                            telegramChat.userSendsCallback(
                                TelegramCallbackData(
                                    id = "callback_id",
                                    action = TelegramMessage.Button.Action.CallBackData("forgottenFlashCardId=${englishVocabularyDataBaseFake.wine.id}"),
                                    messageId = wineFlashCard.id
                                )
                            )
                        }

                        shouldSendDota2FlashCardToTelegram(dota2FlashCard)
                        shouldRemoveWineFlashCardFromTelegramChat(wineFlashCard)
                        shouldDecreaseCounterOnEnglishVocabularyButton(
                            messageId = notificationMessage.id,
                            newNumberToRevise = 1
                        )
                        notificationMessageShouldRemainInSentMessagesDb(notificationMessage.id)
                        wineFlashCardShouldBeForgotten()
                    }

                    And("User recalls Wine FlashCard") {
                        beforeTest {
                            telegramChat.userSendsCallback(
                                TelegramCallbackData(
                                    id = "callback_id",
                                    action = TelegramMessage.Button.Action.CallBackData("recalledFlashCardId=${englishVocabularyDataBaseFake.wine.id}"),
                                    messageId = wineFlashCard.id
                                )
                            )
                        }

                        shouldSendDota2FlashCardToTelegram(dota2FlashCard)
                        shouldRemoveWineFlashCardFromTelegramChat(wineFlashCard)
                        shouldDecreaseCounterOnEnglishVocabularyButton(
                            messageId = notificationMessage.id,
                            newNumberToRevise = 1
                        )
                        notificationMessageShouldRemainInSentMessagesDb(notificationMessage.id)
                        wineFlashCardShouldBeRecalled()
                    }
                }
            }
        }
    }

    /**
     * Reusable Then
     */
    private suspend fun BehaviorSpecWhenContainerScope.shouldDecreaseCounterOnEnglishVocabularyButton(messageId: Long, newNumberToRevise: Int) {
        Then("Should decrease counter on English Vocabulary Button") {
            val editedNotificationMessage = TelegramMessageFake.createTelegramNotification(
                messageId = messageId,
                numberToRevise = newNumberToRevise,
                tableName = englishVocabularyDataBaseFake.name,
                dbId = englishVocabularyDataBaseFake.id
            )
            telegramChat.assertThat()
                .isInChat(editedNotificationMessage)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.notificationMessageShouldRemainInSentMessagesDb(notificationMessageId: Long) {
        Then("Notification Message should remain in Sent Messages DB") {
            sentTelegramMessagesTypeFake.assertThat()
                .presents(notificationMessageId, "NOTIFICATION")
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.notificationMessageShouldRemainIntactInTelegramChat(notificationMessage: TelegramMessage) {
        Then("Notification Message should remain intact in Telegram chat") {
            telegramChat.assertThat()
                .isInChat(notificationMessage)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldSendWineFlashCardToTelegramChat(wineFlashCard: TelegramMessage) {
        Then("Wine Flash Card should be shown") {
            telegramChat.assertThat().isInChat(wineFlashCard)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldRemoveWineFlashCardFromTelegramChat(wineFlashCard: TelegramMessage) {
        Then("Should remove Wine Flash Card From Telegram") {
            telegramChat.assertThat().wasDeleted(wineFlashCard)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldSendDota2FlashCardToTelegram(dota2FlashCard: TelegramMessage) {
        Then("Should Send Dota 2 Flash Card") {
            telegramChat.assertThat().isInChat(dota2FlashCard)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.wineFlashCardShouldBeForgotten() {
        Then("Wine Flash Card should be forgotten") {
            englishVocabularyDataBaseRestfulFake.wine.updatedKnowLevels shouldBe englishVocabularyDataBaseFake.wine.forgottenLevelKnowLevels
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.wineFlashCardShouldBeRecalled() {
        Then("Wine Flash Card should be recalled") {
            englishVocabularyDataBaseRestfulFake.wine.updatedKnowLevels shouldBe englishVocabularyDataBaseFake.wine.nextLevelKnowLevels
        }
    }
}