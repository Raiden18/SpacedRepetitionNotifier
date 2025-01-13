package org.danceofvalkyries.app.domain.usecases

import org.danceofvalkyries.app.data.persistance.notion.database.NotionDatabaseDataBaseTable
import org.danceofvalkyries.app.domain.message.MessageFactory

fun interface AnalyzeFlashCardsAndSendNotificationUseCase {
    suspend fun execute()
}

fun AnalyzeFlashCardsAndSendNotificationUseCase(
    getAllFlashCardsUseCase: GetAllFlashCardsUseCase,
    notionDatabaseDataBaseTable: NotionDatabaseDataBaseTable,
    editNotificationMessageUseCase: EditNotificationMessageUseCase,
    deleteOldAndSendNewNotificationUseCase: DeleteOldAndSendNewNotificationUseCase,
    messageFactory: MessageFactory,
    threshold: Int,
): AnalyzeFlashCardsAndSendNotificationUseCase {
    return AnalyzeFlashCardsAndSendNotificationUseCase {
        val flashCards = getAllFlashCardsUseCase.execute()
        val dataBases = notionDatabaseDataBaseTable.getAll()
        if (flashCards.count() >= threshold) {
            val notificationMessage = messageFactory.createNotification(flashCards, dataBases)
            deleteOldAndSendNewNotificationUseCase.execute(notificationMessage)
        } else {
            val doneMessage = messageFactory.createDone()
            editNotificationMessageUseCase.execute(doneMessage)
        }
    }
}