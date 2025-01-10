package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    spacedRepetitionRepository: SpacedRepetitionDataBaseRepository,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    messageFactory: MessageFactory,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val group = spacedRepetitionRepository.getAll()
        if (group.totalFlashCardsNeedRevising >= threshold) {
            val notificationMessage = messageFactory.createNotification(group)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            val doneMessage = messageFactory.createDone()
            editNotificationMessageUseCase.execute(doneMessage)
        }
    }
}