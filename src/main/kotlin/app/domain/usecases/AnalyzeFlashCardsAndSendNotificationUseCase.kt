package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getAllFlashCardsUseCase: GetAllFlashCardsUseCase,
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val flashCards = getAllFlashCardsUseCase.execute()
        val dataBases = notionDatabaseDataBaseTable.getAll()
        if (flashCards.count() >= threshold) {
            val notificationMessage = NeedRevisingNotificationMessage(flashCards, dataBases)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            editNotificationMessageUseCase.execute(DoneMessage())
        }
    }
}