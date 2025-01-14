package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.data.persistance.telegram_and_notion.TelegramAndNotionIdDao
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi

//TODO: Add tests
class TelegramChatFlashCardView(
    private val sendMessageToTelegramChat: SendMessageToTelegramChat,
    private val telegramChatApi: TelegramChatApi,
    private val getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard,
    private val telegramAndNotionIdDao: TelegramAndNotionIdDao,
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
    }
}