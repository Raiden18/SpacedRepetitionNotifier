package app.domain.usecases

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.DeleteOldAndSendNewNotificationUseCase
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.TelegramMessage

class DeleteOldAndSendNewNotificationUseCaseImplTest : BehaviorSpec() {

    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable = mockk(relaxed = true)
    private val sendMessageToTelegramChat: SendMessageToTelegramChat = mockk(relaxed = true)
    private val telegramChatApi: TelegramChatApi = mockk(relaxed = true)
    private val telegramMessages: TelegramMessages = mockk(relaxed = true)

    private val doneMessage = DoneMessage(
        id = 1
    )
    private val notificationMessage = NeedRevisingNotificationMessage(
        id = 2,
        emptyList(),
        emptyList()
    )
    private val newTgMessageResponse = TelegramMessage(
        id = notificationMessage.id,
        body = notificationMessage.telegramBody
    )

    private lateinit var useCase: DeleteOldAndSendNewNotificationUseCase

    init {
        beforeTest {
            clearAllMocks()
            useCase = DeleteOldAndSendNewNotificationUseCase(
                telegramMessagesDataBaseTable,
                telegramChatApi,
                sendMessageToTelegramChat,
                telegramMessages,
            )
            coEvery { sendMessageToTelegramChat.execute(notificationMessage.telegramBody) } returns newTgMessageResponse
        }

        Given("Done Message is saved in DB") {
            beforeTest {
                coEvery { telegramMessagesDataBaseTable.getMessagesIds() } returns listOf(doneMessage.id)
            }

            When("Tries to replace message") {
                beforeTest {
                    useCase.execute(notificationMessage)
                }

                Then("Should delete DoneMessage from DB") {
                    coVerify(exactly = 1) { telegramMessagesDataBaseTable.deleteFor(doneMessage.id) }
                }

                Then("Should delete DoneMessage From Tg Chat") {
                    coVerify(exactly = 1) { telegramChatApi.deleteFromChat(doneMessage.id) }
                }

                Then("Should send new message to TG") {
                    coVerify(exactly = 1) { sendMessageToTelegramChat.execute(notificationMessage.telegramBody) }
                }

                Then("Should Save Tg Message to DB") {
                    coVerify(exactly = 1) {
                        telegramMessages.add(
                            id = newTgMessageResponse.id,
                            type = notificationMessage.type
                        )
                    }
                }
            }
        }
    }
}