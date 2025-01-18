package integrations

import integrations.testdata.english.vocabulary.EnglishVocabularyDataBaseFake
import integrations.testdata.telegram.TelegramCallbackDataFake
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.app.data.dictionary.constant.ConstantOnlineDictionary
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.users.bot.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.bot.translator.TelegramTextTranslator
import org.danceofvalkyries.utils.resources.EngStringResources
import utils.DispatchersFake
import utils.OnlineDictionariesFake
import utils.SentTelegramMessagesTypeFake
import utils.SqlLiteNotionDataBasesFake
import utils.fakes.httpclient.HttpClientFake
import utils.fakes.telegram.TelegramChatFake
import utils.fakes.telegram.TelegramMessageFake

class ButtonsListenersTest : BehaviorSpec() {

    private lateinit var telegramButtonListenerApp: App
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

                val botUser = TelegramBotUserImpl(
                    telegramChat,
                    sqlLiteNotionDataBasesFake,
                    restfulNotionDataBases,
                    sentTelegramMessagesTypeFake,
                    OnlineDictionariesFake(listOf(cambridgeDictionary)),
                    TelegramTextTranslator(),
                    EngStringResources(),
                )
                telegramButtonListenerApp = AppRunInTestDecorator(
                    TelegramButtonListenerApp(
                        DispatchersFake(),
                        SpaceRepetitionSession(botUser),
                        telegramChat
                    )
                )
            }
            When("User types a message in the chat") {
                lateinit var telegramMessage: TelegramMessage
                lateinit var callback: TelegramCallbackDataFake
                beforeTest {
                    telegramMessage = telegramChat.sendTextMessage(text = "Q", nestedButtons = emptyList())
                    callback = TelegramCallbackDataFake(
                        id = "2",
                        messageId = telegramMessage.id,
                        action = TelegramMessage.Button.Action.Text("Q")
                    )
                    telegramChat.userSendsCallback(callback)
                    telegramButtonListenerApp.run()
                }

                Then("Should delete that message from Telegram") {

                    telegramChat.assertThat().wasDeleted(telegramMessage)
                }

                Then("Should answer to callback") {
                    callback.assertThat().callbackIsAnswered(callback.id)
                }
            }

            When("Notification Message is shown for Flash Cards from English Vocabulary Database") {

                lateinit var notificationMessage: TelegramMessage

                And("There are 2 flash cards to revise") {

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
                        notificationMessage = initAndGetNotificationMessage(numberToRevise = 2)
                        telegramButtonListenerApp.run()
                    }

                    And("User taps on English Vocabulary buttons") {
                        beforeTest {
                            userTapsOnDbButton(
                                dbId = englishVocabularyDataBaseFake.id,
                                notificationMessageId = notificationMessage.id
                            )
                        }

                        shouldSendWineFlashCardToTelegramChat(wineFlashCard)
                        notificationMessageShouldRemainIntactInTelegramChat(notificationMessage)

                        And("User forgets Wine FlashCard") {
                            beforeTest {
                                userTapsOnForgot(
                                    messageId = wineFlashCard.id,
                                    notionPageId = englishVocabularyDataBaseFake.wine.id
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
                                userTapsOnRecalled(
                                    messageId = wineFlashCard.id,
                                    notionPageId = englishVocabularyDataBaseFake.wine.id,
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

                            And("User Recalls Dota 2 Flash Card") {
                                beforeTest {
                                    userTapsOnRecalled(
                                        messageId = dota2FlashCard.id,
                                        notionPageId = englishVocabularyDataBaseFake.dota2.id
                                    )
                                }

                                shouldRemoveDota2FlashCardFromTelegram(dota2FlashCard)
                                shouldEditNotificationMessageToAllDone(messageId = notificationMessage.id)
                                notificationMessageShouldRemainInSentMessagesDb(notificationMessage.id)
                                dota2FlashCardShouldBeRecalled()
                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun initAndGetNotificationMessage(
        numberToRevise: Int
    ): TelegramMessage {
        val message = TelegramMessageFake.createTelegramNotification(
            messageId = -1,
            numberToRevise = numberToRevise,
            tableName = englishVocabularyDataBaseFake.name,
            dbId = englishVocabularyDataBaseFake.id
        )
        val notificationMessage = telegramChat.sendTextMessage(
            text = message.text,
            nestedButtons = message.nestedButtons
        )
        sentTelegramMessagesTypeFake.add(
            id = notificationMessage.id,
            type = "NOTIFICATION"
        )
        return notificationMessage
    }

    private fun userTapsOnDbButton(
        dbId: String,
        notificationMessageId: Long
    ) {
        telegramChat.userSendsCallback(
            TelegramCallbackDataFake(
                id = "callback_id",
                action = TelegramMessage.Button.Action.CallBackData("dbId=${dbId}"),
                messageId = notificationMessageId
            )
        )
    }

    private fun userTapsOnRecalled(messageId: Long, notionPageId: String) {
        telegramChat.userSendsCallback(
            TelegramCallbackDataFake(
                id = "callback_id",
                action = TelegramMessage.Button.Action.CallBackData("recalledFlashCardId=${notionPageId}"),
                messageId = messageId
            )
        )
    }

    private fun userTapsOnForgot(messageId: Long, notionPageId: String) {
        telegramChat.userSendsCallback(
            TelegramCallbackDataFake(
                id = "callback_id",
                action = TelegramMessage.Button.Action.CallBackData("forgottenFlashCardId=${notionPageId}"),
                messageId = messageId
            )
        )
    }

    /**
     * Reusable Then
     */
    private suspend fun BehaviorSpecWhenContainerScope.shouldEditNotificationMessageToAllDone(messageId: Long) {
        Then("Should edit notification message to All done message") {
            val allDoneMessage = TelegramMessageFake.createAllDone(
                messageId = messageId,
            )
            telegramChat.assertThat().isInChat(allDoneMessage)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldDecreaseCounterOnEnglishVocabularyButton(messageId: Long, newNumberToRevise: Int) {
        Then("Should decrease counter on English Vocabulary Button") {
            val editedNotificationMessage = TelegramMessageFake.createTelegramNotification(
                messageId = messageId,
                numberToRevise = newNumberToRevise,
                tableName = englishVocabularyDataBaseFake.name,
                dbId = englishVocabularyDataBaseFake.id
            )
            telegramChat.assertThat().isInChat(editedNotificationMessage)
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
        Then("Should Send Dota 2 Flash Card message") {
            telegramChat.assertThat().isInChat(dota2FlashCard)
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldRemoveDota2FlashCardFromTelegram(dota2FlashCard: TelegramMessage) {
        Then("Should Remove Dota 2 Flash Card Message from Telegram") {
            telegramChat.assertThat().wasDeleted(dota2FlashCard)
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

    private suspend fun BehaviorSpecWhenContainerScope.dota2FlashCardShouldBeForgotten() {
        Then("Dota 2 Flash Card should be forgotten") {
            englishVocabularyDataBaseRestfulFake.dota2.updatedKnowLevels shouldBe englishVocabularyDataBaseFake.dota2.forgottenLevelKnowLevels
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.dota2FlashCardShouldBeRecalled() {
        Then("Dota 2 Flash Card should be recalled") {
            englishVocabularyDataBaseRestfulFake.dota2.updatedKnowLevels shouldBe englishVocabularyDataBaseFake.dota2.nextLevelKnowLevels
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.shouldAnswerToCallback() {
        telegramChat.assertThat()
        TODO()
    }
}