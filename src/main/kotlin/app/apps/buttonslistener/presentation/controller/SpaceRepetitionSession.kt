package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

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

    suspend fun forget(flashCardId: String) {
        humanUser.forget(flashCardId)
        replaceFlashCard()
    }

    suspend fun recall(flashCardId: String) {
        humanUser.recall(flashCardId)
        replaceFlashCard()
    }

    private suspend fun replaceFlashCard() {
        telegramBotUser.updateNotificationMessage()
        val nextFlashCard = telegramBotUser.getAnyFlashCardFor(notionDb.rawValue)
        if (nextFlashCard != null) {
            telegramBotUser.sendFlashCardMessage(nextFlashCard)
        }
        telegramBotUser.removeFlashCards()
    }
}