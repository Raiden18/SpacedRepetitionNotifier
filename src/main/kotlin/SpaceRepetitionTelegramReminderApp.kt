package org.danceofvalkyries

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

class SpaceRepetitionTelegramReminderApp(
    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository,
    private val flashCardsThreshold: Int,
    private val sendRevisingMessage: suspend (SpacedRepetitionDataBaseGroup) -> Unit,
    private val sendGoodJobMessage: suspend () -> Unit,
) {

    suspend fun run() {
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