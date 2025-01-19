package org.danceofvalkyries.job

import integrations.testdata.greek.GreekLettersAndSoundsDataBaseDataBase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.bot.TelegramBotImpl
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.resources.EngStringResources
import utils.DispatchersFake
import utils.OnlineDictionariesFake
import utils.SentTelegramMessagesTypeFake
import utils.SqlLiteNotionDataBasesFake
import utils.fakes.NotionDataBasesRestfulFake
import utils.fakes.telegram.TelegramChatFake
import utils.fakes.telegram.TelegramMessageFake

class NotifierAppTest : BehaviorSpec() {

    private lateinit var notifierJob: Job
    private lateinit var sentTelegramMessagesType: SentTelegramMessagesTypeFake
    private lateinit var sqlLiteNotionDataBases: SqlLiteNotionDataBasesFake
    private lateinit var telegramBot: TelegramBotImpl
    private lateinit var telegramChatFake: TelegramChatFake
    private lateinit var greekLettersAndSoundsDataBaseSqlLiteFake: GreekLettersAndSoundsDataBaseDataBase

    init {
        beforeTest {
            sentTelegramMessagesType = SentTelegramMessagesTypeFake()
            greekLettersAndSoundsDataBaseSqlLiteFake = GreekLettersAndSoundsDataBaseDataBase()
            sqlLiteNotionDataBases = SqlLiteNotionDataBasesFake(listOf(greekLettersAndSoundsDataBaseSqlLiteFake))
            telegramChatFake = TelegramChatFake()
            telegramBot = TelegramBotImpl(
                telegramChatFake,
                sqlLiteNotionDataBases,
                NotionDataBasesRestfulFake(listOf()),
                sentTelegramMessagesType,
                OnlineDictionariesFake(emptyList()),
                EngStringResources(),
                DispatchersFake()
            )
        }

        Given("No notification Messages were sent to Telegram") {
            beforeTest {
                sentTelegramMessagesType.clear()
            }

            When("Number of Flash Cards is more than threshold") {
                beforeTest {
                    notifierJob = createApp(1)
                }

                Then("Should send notification to Telegram") {
                    notifierJob.run()
                    val expectedNotificationMessage = greekLettersAndSoundsDataBaseSqlLiteFake.createTelegramNotification(1)
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
                    notifierJob = createApp(1)
                }

                Then("Should send NEW notification to Telegram") {
                    notifierJob.run()
                    val newNotification = greekLettersAndSoundsDataBaseSqlLiteFake.createTelegramNotification(2)
                    telegramChatFake.assertThat().isInChat(newNotification)
                }

                Then("Should Delete OLD notification From DB") {
                    notifierJob.run()
                    val containsOldMessage = sentTelegramMessagesType.iterate().toList()
                        .firstOrNull { it.id == previousMessage.id }
                    containsOldMessage shouldBe null
                }

                Then("Should Delete OLD notification from Telegram") {
                    notifierJob.run()
                    telegramChatFake.assertThat().wasDeleted(previousMessage)
                }
            }

            When("Number of Flash Cards is less than threshold") {
                beforeTest {
                    notifierJob = createApp(10)
                }

                Then("Then should edit PREVIOUS notification to DONE") {
                    notifierJob.run()
                    val expectedNotificationMessage = TelegramMessageFake.createAllDone(1)
                    telegramChatFake.assertThat()
                        .isInChat(expectedNotificationMessage)
                }
            }

        }
    }

    private fun createApp(flashCardsThreshold: Int): Job {
        return JobRunInTestDecorator(
            NotifierJob(
                DispatchersFake(),
                flashCardsThreshold = flashCardsThreshold,
                sqlLiteNotionDataBases,
                telegramBot,
            )
        )
    }
}