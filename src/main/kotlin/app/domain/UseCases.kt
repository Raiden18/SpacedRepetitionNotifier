package org.danceofvalkyries.app.domain

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.data.db.TelegramMessagesDb

val sendMessageToChatAndSaveToDb: suspend (
    TelegramChatApi,
    TelegramMessagesDb,
    text: String,
) -> Unit = { api, db, text ->
    val telegramMessage = api.sendMessage(text)
    db.save(telegramMessage)
}

val deleteOldMessages: suspend (
    telegramChatApi: TelegramChatApi,
    telegramMessagesDb: TelegramMessagesDb,
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
    telegramMessagesDb: TelegramMessagesDb,
    telegramChatApi: TelegramChatApi,
) -> Unit = { text, db, api ->
    db.getAll().forEach { message ->
        db.update(text, message.id)
        api.editMessageText(message.id, text)
    }
}

val sendReviseOrDoneMessage: suspend (
    SpacedRepetitionDataBaseRepository,
    Int,
    suspend (SpacedRepetitionDataBaseGroup) -> Unit,
    suspend () -> Unit,
) -> Unit = { notionDbs, threshold, sendNotification, sendDone ->
    val spacedRepetitionDbs = notionDbs.getAll()
    if (spacedRepetitionDbs.totalFlashCardsNeedRevising >= threshold) {
        sendNotification(spacedRepetitionDbs)
    } else {
        sendDone.invoke()
    }
}