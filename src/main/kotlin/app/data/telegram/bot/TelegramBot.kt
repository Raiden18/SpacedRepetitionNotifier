package org.danceofvalkyries.app.data.telegram.bot

interface TelegramBot {
    suspend fun editOldNotificationMessageToDoneMessage()
    suspend fun sendNotification()
    suspend fun startRepetitionSessionFor(dbId: String)
    suspend fun makeForgotten(flashCardId: String)
    suspend fun makeRecalled(flashCardId: String)
    suspend fun deleteMessage(telegramMessageId: Long)
}