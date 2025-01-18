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
        println("beginFor: $notionDbId")
        notionDb = NotionId(notionDbId)
        val nextFlashCard = telegramBotUser.getAnyFlashCardFor(notionDb.rawValue)
        telegramBotUser.sendFlashCardMessage(nextFlashCard!!)
    }

    suspend fun forget(flashCardId: String) {
        humanUser.forget(flashCardId)
        replaceFlashCard()
    }

    suspend fun recall(recalledFlashCardID: String) {
        telegramBotUser.removeAllFlashCardsFromChat()
        telegramBotUser.removeRecalledFlashCardFromLocalDbs(recalledFlashCardID)
        telegramBotUser.sendNextFlashCardFrom(notionDb.rawValue)
    }

    private suspend fun replaceFlashCard() {
        //telegramBotUser.updateNotificationMessage()
    }
}