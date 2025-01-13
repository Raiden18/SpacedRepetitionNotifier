package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.usecases.EditNotificationMessageUseCase
import org.danceofvalkyries.telegram.api.EditMessageInTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class EditNotificationMessageUseCaseKtTest : FunSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val editMessageInTelegramChat: EditMessageInTelegramChat = mockk(relaxed = true)
    private lateinit var editNotificationMessageUseCase: EditNotificationMessageUseCase

    private val oldTelegramMessage = TelegramMessage(
        id = 322,
        body = TelegramMessageBody(
            text = "old",
            telegramButtons = emptyList(),
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    )

    init {

        beforeTest {
            clearAllMocks()
            editNotificationMessageUseCase = EditNotificationMessageUseCase(telegramMessagesDataBaseTable, editMessageInTelegramChat)
            coEvery { telegramMessagesDataBaseTable.getAll() } returns listOf(oldTelegramMessage)
        }

        test("Should update message") {
            val newMessageBody = TelegramMessageBody(
                text = "Something new",
                telegramButtons = emptyList(),
                telegramImageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )

            editNotificationMessageUseCase.execute(newMessageBody)

            coVerifyOrder {
                telegramMessagesDataBaseTable.update(newMessageBody, oldTelegramMessage.id)
                editMessageInTelegramChat.execute(newMessageBody, oldTelegramMessage.id)
            }
        }
    }
}