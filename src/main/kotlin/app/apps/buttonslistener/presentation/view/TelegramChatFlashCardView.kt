package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.FlashCardView
import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.sendMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.telegram.api.models.TelegramButton

//TODO: Add tests
class TelegramChatFlashCardView(
    private val telegramChat: TelegramChat,
    private val onlineDictionaries: OnlineDictionaries,
    private val sentNotionPageFlashCardsToTelegram: SentNotionPageFlashCardsToTelegram,
) : FlashCardView {

    override suspend fun show(flashCard: NotionPageFlashCard) {
        val message = FlashCardMessage(
            flashCard,
            onlineDictionaries.iterate(flashCard.notionDbID)
        )
        val telegramMessage = telegramChat.sendMessage(
            text = message.asTelegramBody().text.get(),
            imageUrl = message.asTelegramBody().imageUrl?.get(),
            nestedButtons = message.asTelegramBody().nestedButtons.map {
                it.map { button ->
                    object : TelegramMessage.Button {
                        override val text: String
                            get() = button.text
                        override val action: TelegramMessage.Button.Action
                            get() = when (val action = button.action) {
                                is TelegramButton.Action.Url -> TelegramMessage.Button.Action.Url(action.value)
                                is TelegramButton.Action.CallBackData -> TelegramMessage.Button.Action.CallBackData(action.value)
                            }

                    }
                }
            }
        )
        sentNotionPageFlashCardsToTelegram.add(
            telegramMessage.id,
            flashCard.id
        )
    }

    override suspend fun hide(flashCard: NotionPageFlashCard) {
        val messageId = sentNotionPageFlashCardsToTelegram.iterate()
            .first { it.notionPageId == flashCard.id }
            .messageId
        telegramChat.delete(messageId)
        sentNotionPageFlashCardsToTelegram.delete(messageId)
    }
}