package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    notionDatabases: NotionDataBases,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val flashCards = notionDatabases.iterate()
            .flatMap { it.iterate() }
        if (flashCards.count() >= threshold) {
            val notificationMessage = NeedRevisingNotificationMessage(notionDatabases)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            editNotificationMessageUseCase.execute(DoneMessage())
        }
    }
}