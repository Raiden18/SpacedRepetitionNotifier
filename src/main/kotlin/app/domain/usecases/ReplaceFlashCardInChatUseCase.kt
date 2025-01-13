package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.app.data.persistance.telegram.messages.TelegramMessagesDataBaseTable
import org.danceofvalkyries.app.domain.message.MessageFactory
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
    messageFactory: MessageFactory,
    dispatchers: Dispatchers,
): ReplaceFlashCardInChatUseCase {
    return ReplaceFlashCardInChatUseCase {
        coroutineScope {
            val messages = telegramMessagesDataBaseTable.getAll()
            val flashCardFromChat = messages.firstOrNull { it.body.type == TelegramMessageBody.Type.FLASH_CARD }
            val asyncTasks = mutableListOf<Deferred<Unit>>()
            if (flashCardFromChat != null) {
                val deleteOldAsyncTask = async(dispatchers.io) {
                    deleteMessageFromTelegramChat.execute(flashCardFromChat)
                    telegramMessagesDataBaseTable.delete(flashCardFromChat)
                }
                asyncTasks.add(deleteOldAsyncTask)
            }
            val sendNewMessageAsyncTask = async(dispatchers.io) {
                val messageBody = messageFactory.createFlashCardMessage(it)
                val chatMessage = sendMessageToTelegramChat.execute(messageBody)
                telegramMessagesDataBaseTable.save(chatMessage)
            }
            asyncTasks.add(sendNewMessageAsyncTask)
            asyncTasks.awaitAll()
        }
    }
}