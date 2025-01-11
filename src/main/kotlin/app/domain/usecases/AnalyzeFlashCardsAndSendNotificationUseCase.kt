package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.notion.domain.repositories.FlashCardsTablesRepository

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getFlashCardsTablesUseCase: GetFlashCardsTablesUseCase,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    messageFactory: MessageFactory,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val group = getFlashCardsTablesUseCase.execute()
        if (group.totalFlashCardsNeedRevising >= threshold) {
            val notificationMessage = messageFactory.createNotification(group)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            val doneMessage = messageFactory.createDone()
            editNotificationMessageUseCase.execute(doneMessage)
        }
    }
}