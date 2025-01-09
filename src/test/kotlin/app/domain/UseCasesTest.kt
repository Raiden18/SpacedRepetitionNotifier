package app.domain

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.danceofvalkyries.app.domain.*
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramMessagesDb
import org.danceofvalkyries.telegram.domain.*

class UseCasesTest : FunSpec() {
    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val telegramMessagesDb: TelegramMessagesDb = mockk(relaxed = true)
    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository = mockk(relaxed = true)
    private val sendRevisingMessage: suspend (SpacedRepetitionDataBaseGroup) -> Unit = mockk(relaxed = true)
    private val sendGoodJobMessage: suspend () -> Unit = mockk(relaxed = true)
    private val text = "Message to telegram"

    private val newTelegramMessage = TelegramMessage(
        text = text,
        id = 228,
    )
    private val oldTelegramMessage = TelegramMessage(
        text = text,
        id = 322,
    )

    init {
        beforeTest {
            clearAllMocks()
            coEvery { telegramChatApi.sendMessage(text) } returns newTelegramMessage
            coEvery { telegramMessagesDb.getAll() } returns listOf(oldTelegramMessage)
        }

        test("Should send message and save to db") {
            sendMessageToChatAndSaveToDb(
                telegramChatApi,
                telegramMessagesDb,
                text,
            )

            coVerify(exactly = 1) { telegramMessagesDb.save(newTelegramMessage) }
        }

        test("Should delete old messages in Telegram") {
            deleteOldMessages(
                telegramChatApi,
                telegramMessagesDb
            )

            coVerify(exactly = 1) { telegramChatApi.deleteMessage(oldTelegramMessage.id) }
            coVerify(exactly = 1) { telegramMessagesDb.delete(oldTelegramMessage) }
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

            updateNotificationMessage.invoke(newMessage, telegramMessagesDb, telegramChatApi)

            coVerifyOrder {
                telegramMessagesDb.update(newMessage, oldTelegramMessage.id)
                telegramChatApi.editMessageText(oldTelegramMessage.id, newMessage)
            }
        }


        test("Should send good job notifications if there are less flash cards than threshold") {
            coEvery { spacedRepetitionDataBaseRepository.getAll() } returns SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf()
                    )
                )
            )

            sendReviseOrDoneMessage.invoke(
                spacedRepetitionDataBaseRepository,
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
            coEvery { spacedRepetitionDataBaseRepository.getAll() } returns group

            sendReviseOrDoneMessage.invoke(
                spacedRepetitionDataBaseRepository,
                10,
                sendRevisingMessage,
                sendGoodJobMessage,
            )

            coVerify(exactly = 1) { sendRevisingMessage.invoke(group) }
        }
    }
}