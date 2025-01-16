package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.notion.api.models.NotionId

class FlashCardsController(
    private val spaceRepetitionSession: SpaceRepetitionSession,
    private val telegramBotUser: TelegramBotUser,
) {

    private var notionDb = NotionId.EMPTY

    suspend fun onDataBaseClicked(notionDbId: String) {
        notionDb = NotionId(notionDbId)
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        telegramBotUser.sendFlashCardMessage(nextFlashCard!!)
    }

    suspend fun onForgottenClicked(flashCardId: String) {
        spaceRepetitionSession.forget(NotionId(flashCardId))
        telegramBotUser.updateNotificationMessage()
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (nextFlashCard != null) {
            telegramBotUser.sendFlashCardMessage(nextFlashCard)
        }
        telegramBotUser.removeFlashCards()
    }

    suspend fun onRecalledClicked(flashCardId: String) {
        spaceRepetitionSession.recall(NotionId(flashCardId))
        telegramBotUser.updateNotificationMessage()
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (nextFlashCard != null) {
            telegramBotUser.sendFlashCardMessage(nextFlashCard)
        }
        telegramBotUser.removeFlashCards()
    }
}