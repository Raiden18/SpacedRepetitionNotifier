package org.danceofvalkyries.app.domain

import org.danceofvalkyries.app.domain.srs.SpaceRepetitionSession
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

    suspend fun onForgottenClicked(flashCardId: NotionId, messageId: Long) {
        spaceRepetitionSession.forget(flashCardId)
        val flashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (flashCard != null) {
            flashCardView.show(flashCard)
        }
        flashCardView.hide(messageId)
    }

    suspend fun onRecalledClicked(flashCardId: NotionId, messageId: Long) {
        spaceRepetitionSession.recall(flashCardId)
        val flashCard = spaceRepetitionSession.getNextFlashCard(notionDb)
        if (flashCard != null) {
            flashCardView.show(flashCard)
        }
        flashCardView.hide(messageId)
    }
}