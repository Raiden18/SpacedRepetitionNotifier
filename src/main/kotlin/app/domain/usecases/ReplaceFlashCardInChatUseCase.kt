package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.DeleteMessageFromTelegramChat
import org.danceofvalkyries.telegram.api.SendMessageToTelegramChat
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceFlashCardInChatUseCase {
    suspend fun execute(flashCard: FlashCardNotionPage)
}

fun ReplaceFlashCardInChatUseCase(
    telegramMessagesDataBaseTable: TelegramMessagesDataBaseTable,
    deleteMessageFromTelegramChat: DeleteMessageFromTelegramChat,
    sendMessageToTelegramChat: SendMessageToTelegramChat,
    getOnlineDictionariesForFlashCard: GetOnlineDictionariesForFlashCard,
    dispatchers: Dispatchers,
): ReplaceFlashCardInChatUseCase {
    return ReplaceFlashCardInChatUseCase {
        coroutineScope {
            val messages = telegramMessagesDataBaseTable.getAll()
            val flashCardFromChat = messages.firstOrNull() // TODO
            val asyncTasks = mutableListOf<Deferred<Unit>>()
            if (flashCardFromChat != null) {
                val deleteOldAsyncTask = async(dispatchers.io) {
                    deleteMessageFromTelegramChat.execute(flashCardFromChat)
                    telegramMessagesDataBaseTable.delete(flashCardFromChat)
                }
                asyncTasks.add(deleteOldAsyncTask)
            }
            val sendNewMessageAsyncTask = async(dispatchers.io) {
                val onlineDictionaries = getOnlineDictionariesForFlashCard.execute(it)
                val messageBody = FlashCardMessage(it, onlineDictionaries)
                val chatMessage = sendMessageToTelegramChat.execute(messageBody.telegramBody)
                telegramMessagesDataBaseTable.save(chatMessage, messageBody.type)
            }
            asyncTasks.add(sendNewMessageAsyncTask)
            asyncTasks.awaitAll()
        }
    }
}