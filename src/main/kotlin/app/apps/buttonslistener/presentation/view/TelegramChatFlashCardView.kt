package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi

//TODO: Add tests
class TelegramChatFlashCardView(
    private val sendMessageToTelegramChat: SendMessageToTelegramChat,
    private val telegramChatApi: TelegramChatApi,
    private val getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard,
) : FlashCardView {

    override suspend fun show(flashCard: FlashCardNotionPage) {
        val message = FlashCardMessage(
            flashCard,
            getOnlineDictionariesForFlashCard.execute(flashCard)
        )
        sendMessageToTelegramChat.execute(message.telegramBody)
    }

    override suspend fun hide(messageId: Long) {
        telegramChatApi.deleteFromChat(messageId)
    }
}