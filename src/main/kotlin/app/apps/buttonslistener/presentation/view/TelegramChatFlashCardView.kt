package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.app.domain.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

//TODO: Add tests
class TelegramChatFlashCardView(
    private val sendMessageToTelegramChat: SendMessageToTelegramChat,
    private val telegramChatApi: TelegramChatApi,
    private val getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard,
    private val sentNotionPageFlashCardsToTelegram: SentNotionPageFlashCardsToTelegram,
) : FlashCardView {

    override suspend fun show(flashCard: NotionPageFlashCard) {
        val message = FlashCardMessage(
            flashCard,
            getOnlineDictionariesForFlashCard.execute(flashCard)
        )
        val telegramMessage = sendMessageToTelegramChat.execute(message.asTelegramBody())
        sentNotionPageFlashCardsToTelegram.add(
            telegramMessage.id,
            flashCard.id
        )
    }

    override suspend fun hide(flashCard: NotionPageFlashCard) {
        val messageId = sentNotionPageFlashCardsToTelegram.iterate()
            .first { it.notionPageId == flashCard.id }
            .messageId

        telegramChatApi.deleteFromChat(messageId)
        sentNotionPageFlashCardsToTelegram.delete(messageId)
    }
}