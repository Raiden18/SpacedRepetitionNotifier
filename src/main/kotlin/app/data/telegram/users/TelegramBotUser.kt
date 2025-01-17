package org.danceofvalkyries.app.data.telegram.users

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

interface TelegramBotUser {
    suspend fun editOldNotificationMessageToDoneMessage()
    suspend fun deleteOldNotificationMessage()
    suspend fun sendNewNotificationMessage()
    suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard)
    suspend fun getAnyFlashCardFor(notionDbId: String): NotionPageFlashCard?
    suspend fun removeFlashCards()
    suspend fun updateNotificationMessage()
    suspend fun deleteMessage(messageId: Long)

    suspend fun removeFromDB(flashCardId: String)
    suspend fun removeAllFlashCardsFromChat()
    suspend fun sendNextFlashCardFrom(notionDbId: String)
}