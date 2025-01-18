package org.danceofvalkyries.app.data.telegram.users.bot

import org.danceofvalkyries.app.data.telegram.users.HumanUser
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.notion.api.models.NotionId

class SpaceRepetitionSession(
    private val humanUser: HumanUser,
    private val telegramBotUser: TelegramBotUser,
) {

    private var notionDb = NotionId.EMPTY

    suspend fun beginFor(notionDbId: String) {
        notionDb = NotionId(notionDbId)
        val nextFlashCard = telegramBotUser.getAnyFlashCardFor(notionDb.rawValue)
        telegramBotUser.sendFlashCardMessage(nextFlashCard!!)
    }

    suspend fun forget(forgotFlashCardId: String) {
        telegramBotUser.removeAllFlashCardsFromChat()
        telegramBotUser.removeForgotFlashCardFromLocalDbs(forgotFlashCardId)
        telegramBotUser.sendNextFlashCardFrom(notionDb.rawValue)
        telegramBotUser.updateNotificationMessage()
    }

    suspend fun recall(recalledFlashCardID: String) {
        telegramBotUser.removeAllFlashCardsFromChat()
        telegramBotUser.removeRecalledFlashCardFromLocalDbs(recalledFlashCardID)
        telegramBotUser.sendNextFlashCardFrom(notionDb.rawValue)
        telegramBotUser.updateNotificationMessage()
    }
}