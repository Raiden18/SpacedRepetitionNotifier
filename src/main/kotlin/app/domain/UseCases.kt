package org.danceofvalkyries.app.domain

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.domain.TelegramChatRepository
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

val deleteOldAndSendNewNotification: suspend (
    TelegramChatRepository,
    TelegramNotificationMessageDb,
    textBody: TelegramMessageBody,
) -> Unit = { repository, db, messageBody ->
    db.getAll().forEach { oldMessage ->
        repository.deleteFromChat(oldMessage)
        db.delete(oldMessage)
    }
    val telegramMessage = repository.sendToChat(messageBody)
    repository.saveToDb(telegramMessage)
}

val editNotificationMessage: suspend (
    String,
    telegramNotificationMessageDb: TelegramNotificationMessageDb,
    telegramChatApi: TelegramChatApi,
) -> Unit = { text, db, api ->
    db.getAll().forEach { message ->
        db.update(text, message.id)
        api.editMessageText(message.id, text)
    }
}

val sendReviseOrDoneMessage: suspend (
    SpacedRepetitionDataBaseGroup,
    Int,
    suspend (SpacedRepetitionDataBaseGroup) -> Unit,
    suspend () -> Unit,
) -> Unit = { spacedRepetitionDbs, threshold, sendNotification, sendDone ->
    if (spacedRepetitionDbs.totalFlashCardsNeedRevising >= threshold) {
        sendNotification(spacedRepetitionDbs)
    } else {
        sendDone()
    }
}