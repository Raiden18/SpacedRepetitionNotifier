package org.danceofvalkyries.app.domain

import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.app.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.impl.TelegramChatApi

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

    override suspend fun finish(messageId: Long) {
        telegramChatApi.deleteFromChat(messageId)
    }
}