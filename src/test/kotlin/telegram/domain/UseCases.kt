package telegram.domain

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramMessagesDb
import org.danceofvalkyries.telegram.domain.*

class UseCases : FunSpec() {
    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val telegramMessagesDb: TelegramMessagesDb = mockk(relaxed = true)
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
    }
}