package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.TelegramChatApi

//TODO: Add tests
class TelegramChatFlashCardView(
    private val sendMessageToTelegramChat: SendMessageToTelegramChat,
    private val telegramChatApi: TelegramChatApi,
    private val onlineDictionaries: OnlineDictionaries,
    private val sentNotionPageFlashCardsToTelegram: SentNotionPageFlashCardsToTelegram,
) : FlashCardView {

    override suspend fun show(flashCard: NotionPageFlashCard) {
        val message = FlashCardMessage(
            flashCard,
            onlineDictionaries.iterate(flashCard.notionDbID)
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