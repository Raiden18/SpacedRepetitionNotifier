package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

fun interface ReplaceFlashCardInChatUseCase {
    suspend fun execute(flashCard: FlashCard)
}

fun ReplaceFlashCardInChatUseCase(
    telegramChatRepository: TelegramChatRepository,
    messageFactory: MessageFactory,
): ReplaceFlashCardInChatUseCase {
    return ReplaceFlashCardInChatUseCase {
        val messages = telegramChatRepository.getAllFromDb()
        val flashCardFromChat = messages.firstOrNull { it.body.type == TelegramMessageBody.Type.FLASH_CARD }
        if (flashCardFromChat != null) {
            telegramChatRepository.deleteFromChat(flashCardFromChat)
            telegramChatRepository.deleteFromDb(flashCardFromChat)
        }
        val messageBody = messageFactory.createFlashCardMessage(it)
        val chatMessage = telegramChatRepository.sendToChat(messageBody)
        telegramChatRepository.saveToDb(chatMessage)
    }
}