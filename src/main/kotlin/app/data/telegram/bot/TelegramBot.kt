package org.danceofvalkyries.app.data.telegram.bot

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

interface TelegramBot {
    suspend fun editOldNotificationMessageToDoneMessage()
    suspend fun deleteOldNotificationMessage()
    suspend fun sendNewNotificationMessage()
    suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard)
    suspend fun getAnyFlashCardFor(notionDbId: String): NotionPageFlashCard?
    suspend fun updateNotificationMessage()
    suspend fun deleteAllFlashCardsFromChat()
    suspend fun sendNextFlashCardFrom(notionDbId: String)
    suspend fun removeRecalledFlashCardFromLocalDbs(recalledFlashCardID: String)
    suspend fun removeForgotFlashCardFromLocalDbs(forgotFlashCardId: String)
    suspend fun makeForgottenOnNotion(flashCardId: String)
    suspend fun makeRecalledOnNotion(flashCardId: String)

    suspend fun startRepetitionSessionFor(dbId: String)
    suspend fun makeForgotten(flashCardId: String)
    suspend fun makeRecalled(flashCardId: String)
    suspend fun deleteMessage(telegramMessageId: Long)
}