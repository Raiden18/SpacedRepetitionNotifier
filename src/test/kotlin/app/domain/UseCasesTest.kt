package app.domain

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.danceofvalkyries.app.domain.*
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.domain.TelegramMessage
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

class UseCasesTest : FunSpec() {
    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val telegramNotificationMessageDb: TelegramNotificationMessageDb = mockk(relaxed = true)
    private val sendRevisingMessage: suspend (SpacedRepetitionDataBaseGroup) -> Unit = mockk(relaxed = true)
    private val sendGoodJobMessage: suspend () -> Unit = mockk(relaxed = true)
    private val text = "Message to telegram"

    private val newTelegramMessage = TelegramMessage(
        id = 228,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList()
        )
    )
    private val oldTelegramMessage = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = text,
            buttons = emptyList()
        )
    )

    init {
        beforeTest {
            clearAllMocks()
            coEvery { telegramChatApi.sendMessage(newTelegramMessage.body) } returns newTelegramMessage
            coEvery { telegramNotificationMessageDb.getAll() } returns listOf(oldTelegramMessage)
        }

        test("Should send message and save to db") {
            sendMessageToChatAndSaveToDb(
                telegramChatApi,
                telegramNotificationMessageDb,
                newTelegramMessage.body,
            )

            coVerify(exactly = 1) { telegramNotificationMessageDb.save(newTelegramMessage) }
        }

        test("Should delete old messages in Telegram") {
            deleteOldMessages(
                telegramChatApi,
                telegramNotificationMessageDb
            )

            coVerify(exactly = 1) { telegramChatApi.deleteMessage(oldTelegramMessage.id) }
            coVerify(exactly = 1) { telegramNotificationMessageDb.delete(oldTelegramMessage) }
        }

        test("Should update old message with new one") {
            val deleteOld: suspend () -> Unit = mockk(relaxed = true)
            val sendNew: suspend () -> Unit = mockk(relaxed = true)

            replaceNotificationMessage(deleteOld, sendNew)

            coVerify {
                deleteOld.invoke()
                sendNew.invoke()
            }
        }

        test("Should update message") {
            val newMessage = "Something new"

            updateNotificationMessage.invoke(newMessage, telegramNotificationMessageDb, telegramChatApi)

            coVerifyOrder {
                telegramNotificationMessageDb.update(newMessage, oldTelegramMessage.id)
                telegramChatApi.editMessageText(oldTelegramMessage.id, newMessage)
            }
        }


        test("Should send good job notifications if there are less flash cards than threshold") {
            val group = SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf()
                    )
                )
            )

            sendReviseOrDoneMessage.invoke(
                group,
                10,
                sendRevisingMessage,
                sendGoodJobMessage,
            )


            coVerify(exactly = 1) { sendGoodJobMessage.invoke() }
        }

        test("Should send notifications if there are more flash cards than threshold") {
            val group = SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf(
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard
                        )
                    )
                )
            )

            sendReviseOrDoneMessage.invoke(
                group,
                10,
                sendRevisingMessage,
                sendGoodJobMessage,
            )

            coVerify(exactly = 1) { sendRevisingMessage.invoke(group) }
        }
    }
}