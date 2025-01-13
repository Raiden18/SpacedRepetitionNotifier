package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.usecases.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.telegram.api.DeleteMessageFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class DeleteOldAndSendNewNotificationUseCaseImplTest : FunSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val deleteMessageFromTelegramChat: DeleteMessageFromTelegramChat = mockk(relaxed = true)
    private val sendMessageToTelegramChat: SendMessageToTelegramChat = mockk(relaxed = true)

    private val text = "Message to telegram"

    private val telegramNotification = TelegramMessage(
        id = 228,
        body = TelegramMessageBody(
            text = text,
            telegramButtons = emptyList(),
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )
    private val oldTelegramNotification = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = text,
            telegramButtons = emptyList(),
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION
        )
    )
    private val flashCardMessage = TelegramMessage(
        id = 1,
        body = TelegramMessageBody.EMPTY.copy(
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    )

    private lateinit var useCase: DeleteOldAndSendNewNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            useCase = DeleteOldAndSendNewNotificationUseCase(
                telegramMessagesDataBaseTable,
                deleteMessageFromTelegramChat,
                sendMessageToTelegramChat,
            )
            coEvery { sendMessageToTelegramChat.execute(telegramNotification.body) } returns telegramNotification
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(oldTelegramNotification, flashCardMessage)
        }

        test("Should replace old tg message with new one") {

            useCase.execute(telegramNotification.body)

            coVerify(exactly = 1) { telegramMessagesDataBaseTable.delete(oldTelegramNotification) }
            coVerify(exactly = 1) { deleteMessageFromTelegramChat.execute(oldTelegramNotification) }

            coVerify(exactly = 1) { telegramMessagesDataBaseTable.save(telegramNotification) }
            coVerify(exactly = 1) { sendMessageToTelegramChat.execute(telegramNotification.body) }

            coVerify(exactly = 0) { telegramMessagesDataBaseTable.delete(flashCardMessage) }
            coVerify(exactly = 0) { deleteMessageFromTelegramChat.execute(flashCardMessage) }
        }
    }
}