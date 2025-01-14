package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs.SpaceRepetitionSession
import org.danceofvalkyries.notion.api.models.NotionId

//TODO: Add tests
class FlashCardsController(
    private val spaceRepetitionSession: SpaceRepetitionSession,
    private val flashCardView: FlashCardView,
) {

    private var notionDb = NotionId.EMPTY

    suspend fun onDataBaseClicked(notionDbId: NotionId) {
        notionDb = notionDbId
        val flashCard = spaceRepetitionSession.getNextFlashCard(notionDbId)
        flashCardView.show(flashCard!!)
    }

    suspend fun onForgottenClicked(flashCardId: NotionId) {
        val currentFlashCard = spaceRepetitionSession.getCurrentFlashCard(flashCardId)
        spaceRepetitionSession.forget(flashCardId)
        val flashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (flashCard != null) {
            flashCardView.show(flashCard)
        }
        flashCardView.hide(currentFlashCard)
    }

    suspend fun onRecalledClicked(flashCardId: NotionId) {
        val currentFlashCard = spaceRepetitionSession.getCurrentFlashCard(flashCardId)
        spaceRepetitionSession.recall(flashCardId)
        val flashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (flashCard != null) {
            flashCardView.show(flashCard)
        }
        flashCardView.hide(currentFlashCard)
    }
}