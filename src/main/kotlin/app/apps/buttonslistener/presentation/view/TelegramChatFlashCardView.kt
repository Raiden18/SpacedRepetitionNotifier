package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.data.persistance.telegram_and_notion.TelegramAndNotionIdDao
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

//TODO: Add tests
class TelegramChatFlashCardView(
    private val sendMessageToTelegramChat: SendMessageToTelegramChat,
    private val telegramChatApi: TelegramChatApi,
    private val getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard,
    private val telegramAndNotionIdDao: TelegramAndNotionIdDao,
    private val telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    private val notionPageFlashCardDataBaseTable: NotionPageFlashCardDataBaseTable,
    private val notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
) : FlashCardView {

    override suspend fun show(flashCard: FlashCardNotionPage) {
        val message = FlashCardMessage(
            flashCard,
            getOnlineDictionariesForFlashCard.execute(flashCard)
        )
        val telegramMessage = sendMessageToTelegramChat.execute(message.telegramBody)
        telegramAndNotionIdDao.save(flashCard.id, telegramMessage.id)
    }

    override suspend fun hide(flashCard: FlashCardNotionPage) {
        val messageId = telegramAndNotionIdDao.getMessageIdBy(flashCard.id)
        telegramChatApi.deleteFromChat(messageId)
        telegramAndNotionIdDao.deleteBy(messageId)

        val notificationMessage = telegramMessagesDataBaseTable.getMessagesIds().first { telegramMessagesDataBaseTable.getTypeFor(it) == NotificationMessage.TYPE_NAME }

        // TODO: Add Notification View?
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(
                flashCards =  notionDatabaseDataBaseTable.getAll()
                    .map { it.id }
                    .flatMap { notionPageFlashCardDataBaseTable.getAllFor(it) },
                notionDataBases = notionDatabaseDataBaseTable.getAll()
            ).telegramBody,
            notificationMessage
        )
    }
}