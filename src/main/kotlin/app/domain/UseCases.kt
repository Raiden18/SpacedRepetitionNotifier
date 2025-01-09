package org.danceofvalkyries.app.domain

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramNotificationMessageDb
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

val sendMessageToChatAndSaveToDb: suspend (
    TelegramChatApi,
    TelegramNotificationMessageDb,
    textBody: TelegramMessageBody,
) -> Unit = { api, db, text ->
    val telegramMessage = api.sendMessage(text)
    db.save(telegramMessage)
}

val deleteOldMessages: suspend (
    telegramChatApi: TelegramChatApi,
    telegramNotificationMessageDb: TelegramNotificationMessageDb,
) -> Unit = { api, db ->
    db.getAll().forEach { oldMessage ->
        api.deleteMessage(oldMessage.id)
        db.delete(oldMessage)
    }
}

val replaceNotificationMessage: suspend (
    deleteOld: suspend () -> Unit,
    sendNew: suspend () -> Unit,
) -> Unit = { deleteOld, sendNew ->
    deleteOld.invoke()
    sendNew.invoke()
}

val updateNotificationMessage: suspend (
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