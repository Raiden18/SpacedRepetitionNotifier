package integrations

import integrations.testdata.greek.GreekLettersAndSoundsDataBaseRestfulFake
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.bot.translator.TelegramTextTranslator
import org.danceofvalkyries.utils.resources.EngStringResources
import utils.DispatchersFake
import utils.OnlineDictionariesFake
import utils.SentTelegramMessagesTypeFake
import utils.SqlLiteNotionDataBasesFake
import utils.fakes.NotionDataBasesRestfulFake
import utils.fakes.telegram.TelegramChatFake
import utils.fakes.telegram.TelegramMessageFake

class NotifierAppTest : BehaviorSpec() {

    private lateinit var notifierApp: App
    private lateinit var sentTelegramMessagesType: SentTelegramMessagesTypeFake
    private lateinit var sqlLiteNotionDataBases: SqlLiteNotionDataBasesFake
    private lateinit var greekLettersAndSoundsDataBaseRestfulFake: GreekLettersAndSoundsDataBaseRestfulFake
    private lateinit var restfulNotionDataBases: NotionDataBasesRestfulFake
    private lateinit var telegramBot: TelegramBotUserImpl
    private lateinit var telegramChatFake: TelegramChatFake

    init {
        beforeTest {
            sentTelegramMessagesType = SentTelegramMessagesTypeFake()
            sqlLiteNotionDataBases = SqlLiteNotionDataBasesFake()
            greekLettersAndSoundsDataBaseRestfulFake = GreekLettersAndSoundsDataBaseRestfulFake()
            telegramChatFake = TelegramChatFake()
            restfulNotionDataBases = NotionDataBasesRestfulFake(listOf(greekLettersAndSoundsDataBaseRestfulFake))
            telegramBot = TelegramBotUserImpl(
                telegramChatFake,
                sqlLiteNotionDataBases,
                restfulNotionDataBases,
                sentTelegramMessagesType,
                OnlineDictionariesFake(emptyList()),
                TelegramTextTranslator(),
                EngStringResources(),
            )
        }

        Given("No notification Messages were sent to Telegram") {
            beforeTest {
                sentTelegramMessagesType.clear()
            }

            When("Number of Flash Cards is more than threshold") {
                beforeTest {
                    notifierApp = createApp(1)
                }

                Then("Should send notification to Telegram") {
                    notifierApp.run()
                    val expectedNotificationMessage = greekLettersAndSoundsDataBaseRestfulFake.createTelegramNotification(1)
                    telegramChatFake.assertThat().isInChat(expectedNotificationMessage)
                }
            }
        }

        Given("Notification Message was already sent") {
            lateinit var previousMessage: TelegramMessage

            beforeTest {
                previousMessage = telegramChatFake.sendTextMessage(
                    "Previous Message",
                    nestedButtons = emptyList()
                )
                sentTelegramMessagesType.add(
                    id = previousMessage.id,
                    type = "NOTIFICATION"
                )
            }

            When("Number of Flash Cards is more than threshold") {
                beforeTest {
                    notifierApp = createApp(1)
                }

                Then("Should send NEW notification to Telegram") {
                    notifierApp.run()
                    val newNotification = greekLettersAndSoundsDataBaseRestfulFake.createTelegramNotification(2)
                    telegramChatFake.assertThat().isInChat(newNotification)
                }

                Then("Should Delete OLD notification From DB") {
                    notifierApp.run()
                    val containsOldMessage = sentTelegramMessagesType.iterate().toList()
                        .firstOrNull { it.id == previousMessage.id }
                    containsOldMessage shouldBe null
                }

                Then("Should Delete OLD notification from Telegram") {
                    notifierApp.run()
                    telegramChatFake.assertThat().wasDeleted(previousMessage)
                }
            }

            When("Number of Flash Cards is less than threshold") {
                beforeTest {
                    notifierApp = createApp(10)
                }

                Then("Then should edit PREVIOUS notification to DONE") {
                    notifierApp.run()
                    val expectedNotificationMessage = TelegramMessageFake.createAllDone(1)
                    telegramChatFake.assertThat()
                        .isInChat(expectedNotificationMessage)
                }
            }

        }
    }

    private fun createApp(flashCardsThreshold: Int): App {
        return AppRunInTestDecorator(
            NotifierApp(
                DispatchersFake(),
                flashCardsThreshold = flashCardsThreshold,
                restfulNotionDataBases,
                sqlLiteNotionDataBases,
                telegramBot,
            )
        )
    }
}