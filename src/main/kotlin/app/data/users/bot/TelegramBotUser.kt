package org.danceofvalkyries.app.data.users.bot

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

interface TelegramBotUser {
    suspend fun editOldNotificationMessageToDoneMessage()
    suspend fun deleteOldNotificationMessage()
    suspend fun sendNewNotificationMessage()
    suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard)
    suspend fun removeFlashCards()
}