package org.danceofvalkyries.app

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

class AppImpl(
    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository,
    private val flashCardsThreshold: Int,
    private val sendRevisingMessage: suspend (SpacedRepetitionDataBaseGroup) -> Unit,
    private val sendGoodJobMessage: suspend () -> Unit,
) : App {

    override suspend fun run() {
        checkSpaceRepetitionDatabasesAndSendNotificationIfNeeded()
    }

    private suspend fun checkSpaceRepetitionDatabasesAndSendNotificationIfNeeded() {
        val spacedRepetitionDbs = spacedRepetitionDataBaseRepository.getAll()
        if (spacedRepetitionDbs.totalFlashCardsNeedRevising >= flashCardsThreshold) {
            sendRevisingMessage(spacedRepetitionDbs)
        } else {
            sendGoodJobMessage.invoke()
        }
    }
}