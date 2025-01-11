package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getAllFlashCardsUseCase: GetAllFlashCardsUseCase,
    getAllNotionDatabasesUseCase: GetAllNotionDatabasesUseCase,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    messageFactory: MessageFactory,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val flashCards = getAllFlashCardsUseCase.execute()
        val dataBases = getAllNotionDatabasesUseCase.execute()
        if (flashCards.count() >= threshold) {
            val notificationMessage = messageFactory.createNotification(flashCards, dataBases)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            val doneMessage = messageFactory.createDone()
            editNotificationMessageUseCase.execute(doneMessage)
        }
    }
}