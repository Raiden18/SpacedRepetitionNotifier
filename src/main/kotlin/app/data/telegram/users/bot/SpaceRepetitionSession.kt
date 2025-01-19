package org.danceofvalkyries.app.data.telegram.users.bot

import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.notion.api.models.NotionId

class SpaceRepetitionSession(
    private val telegramBotUser: TelegramBotUser,
) {

    private var notionDb = NotionId.EMPTY

    suspend fun beginFor(notionDbId: String) {
        notionDb = NotionId(notionDbId)
        val nextFlashCard = telegramBotUser.getAnyFlashCardFor(notionDb.rawValue)
        telegramBotUser.sendFlashCardMessage(nextFlashCard!!)
    }

    suspend fun forget(forgotFlashCardId: String) {
        telegramBotUser.makeForgottenOnNotion(forgotFlashCardId)
        telegramBotUser.deleteAllFlashCardsFromChat()
        telegramBotUser.removeForgotFlashCardFromLocalDbs(forgotFlashCardId)
        telegramBotUser.sendNextFlashCardFrom(notionDb.rawValue)
        telegramBotUser.updateNotificationMessage()
    }

    suspend fun recall(recalledFlashCardID: String) {
        telegramBotUser.makeRecalledOnNotion(recalledFlashCardID)
        telegramBotUser.deleteAllFlashCardsFromChat()
        telegramBotUser.removeRecalledFlashCardFromLocalDbs(recalledFlashCardID)
        telegramBotUser.sendNextFlashCardFrom(notionDb.rawValue)
        telegramBotUser.updateNotificationMessage()
    }
}