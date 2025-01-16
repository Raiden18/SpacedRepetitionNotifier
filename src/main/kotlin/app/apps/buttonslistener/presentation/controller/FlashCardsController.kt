package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSession
import org.danceofvalkyries.notion.api.models.NotionId

//TODO: Add tests
class FlashCardsController(
    private val spaceRepetitionSession: SpaceRepetitionSession,
    private val flashCardView: FlashCardView,
    private val notificationView: NotificationView,
) {

    private var notionDb = NotionId.EMPTY

    suspend fun onDataBaseClicked(notionDbId: String) {
        notionDb = NotionId(notionDbId)
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        flashCardView.show(nextFlashCard!!)
    }

    suspend fun onForgottenClicked(flashCardId: String) {
        val currentFlashCard = spaceRepetitionSession.getCurrentFlashCard(NotionId(flashCardId))
        spaceRepetitionSession.forget(NotionId(flashCardId))
        notificationView.update()
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (nextFlashCard != null) {
            flashCardView.show(nextFlashCard)
        }
        flashCardView.hide(currentFlashCard)
    }

    suspend fun onRecalledClicked(flashCardId: String) {
        val currentFlashCard = spaceRepetitionSession.getCurrentFlashCard(NotionId(flashCardId))
        spaceRepetitionSession.recall(NotionId(flashCardId))
        notificationView.update()
        val nextFlashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (nextFlashCard != null) {
            flashCardView.show(nextFlashCard)
        }
        flashCardView.hide(currentFlashCard)
    }
}