package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

fun interface SendFlashCardToChatUseCase {
    suspend fun execute(flashCard: FlashCard)
}

fun SendFlashCardToChatUseCase(
    telegramChatRepository: TelegramChatRepository,
    messageFactory: MessageFactory,
): SendFlashCardToChatUseCase {
    return SendFlashCardToChatUseCase {
        val messages = telegramChatRepository.getAllFromDb()
        val flashCardFromChat = messages.firstOrNull { it.body.type == TelegramMessageBody.Type.FLASH_CARD }
        val messageBody = messageFactory.createFlashCardMessage(it)
        if (flashCardFromChat == null) {
            val chatMessage = telegramChatRepository.sendToChat(messageBody)
            telegramChatRepository.saveToDb(chatMessage)
        } else {
            telegramChatRepository.editInChat(messageBody, flashCardFromChat.id)
            telegramChatRepository.updateInDb(messageBody, flashCardFromChat.id)
        }
    }
}