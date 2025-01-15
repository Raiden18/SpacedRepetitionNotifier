package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.EditNotificationMessageUseCase
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.TelegramChatApi

class EditNotificationMessageUseCaseKtTest : FunSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val telegramApi: TelegramChatApi = mockk(relaxed = true)
    private lateinit var editNotificationMessageUseCase: EditNotificationMessageUseCase

    private val oldTelegramMessage = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = "old",
            telegramButtons = emptyList(),
            telegramImageUrl = null,
        )
    )

    init {

        beforeTest {
            clearAllMocks()
            editNotificationMessageUseCase = EditNotificationMessageUseCase(telegramMessagesDataBaseTable, telegramApi)
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(oldTelegramMessage)
        }

        test("Should update message") {
            val newMessage = DoneMessage()

            editNotificationMessageUseCase.execute(newMessage)

            coVerifyOrder {
                telegramMessagesDataBaseTable.update(newMessage.telegramBody, oldTelegramMessage.id)
                telegramApi.editInChat(newMessage.telegramBody, oldTelegramMessage.id)
            }
        }
    }
}