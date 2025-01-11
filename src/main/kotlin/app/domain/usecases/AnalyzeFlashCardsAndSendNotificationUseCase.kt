package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getAllFlashCardsUseCase: GetAllFlashCardsUseCase,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    messageFactory: MessageFactory,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        if (getAllFlashCardsUseCase.execute().count() >= threshold) {
            val notificationMessage = messageFactory.createNotification(group)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            val doneMessage = messageFactory.createDone()
            editNotificationMessageUseCase.execute(doneMessage)
        }
    }
}