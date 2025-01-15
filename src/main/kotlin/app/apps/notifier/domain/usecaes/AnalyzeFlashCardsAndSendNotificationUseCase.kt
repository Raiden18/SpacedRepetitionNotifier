package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getAllFlashCardsUseCase: GetAllFlashCardsUseCase,
    notionDatabases: NotionDataBases,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val flashCards = getAllFlashCardsUseCase.execute()
        val dataBases = notionDatabases.iterate()
            .map {
                NotionDataBase(
                    id = NotionId(it.id),
                    name = it.name,
                )
            }
            .toList()
        if (flashCards.count() >= threshold) {
            val notificationMessage = NeedRevisingNotificationMessage(flashCards, dataBases)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            editNotificationMessageUseCase.execute(DoneMessage())
        }
    }
}