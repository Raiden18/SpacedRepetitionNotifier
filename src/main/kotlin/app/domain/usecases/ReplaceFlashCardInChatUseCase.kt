package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardInChatUseCase {
    suspend fun execute(flashCard: FlashCard)
}

fun ReplaceFlashCardInChatUseCase(
    telegramChatRepository: TelegramChatRepository,
    messageFactory: MessageFactory,
    dispatchers: Dispatchers,
): ReplaceFlashCardInChatUseCase {
    return ReplaceFlashCardInChatUseCase {
        coroutineScope {
            val messages = telegramChatRepository.getAllFromDb()
            val flashCardFromChat = messages.firstOrNull { it.body.type == TelegramMessageBody.Type.FLASH_CARD }
            val asyncTasks = mutableListOf<Deferred<Unit>>()
            if (flashCardFromChat != null) {
                val deleteOldAsyncTask = async(dispatchers.io) {
                    telegramChatRepository.deleteFromChat(flashCardFromChat)
                    telegramChatRepository.deleteFromDb(flashCardFromChat)
                }
                asyncTasks.add(deleteOldAsyncTask)
            }
            val sendNewMessageAsyncTask = async(dispatchers.io) {
                val messageBody = messageFactory.createFlashCardMessage(it)
                val chatMessage = telegramChatRepository.sendToChat(messageBody)
                telegramChatRepository.saveToDb(chatMessage)
            }
            asyncTasks.add(sendNewMessageAsyncTask)
            asyncTasks.awaitAll()
        }
    }
}