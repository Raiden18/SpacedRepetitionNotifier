package org.danceofvalkyries

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

class SpaceRepetitionTelegramReminderApp(
    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository,
    private val flashCardsThreshold: Int,
    private val buildMessage: (SpacedRepetitionDataBaseGroup) -> String,
    private val sendMessage: suspend (String) -> Unit,
) {

    suspend fun run() {
        tryToCheckSpaceRepetitionDatabasesAndSendNotificationIfNeeded()
    }

    private suspend fun tryToCheckSpaceRepetitionDatabasesAndSendNotificationIfNeeded() {
        try {
            checkSpaceRepetitionDatabasesAndSendNotificationIfNeeded()
        } catch (throwable: Throwable) {
            sendMessage.invoke(throwable.stackTraceToString())
        }
    }

    private suspend fun checkSpaceRepetitionDatabasesAndSendNotificationIfNeeded() {
        val spacedRepetitionDbs = spacedRepetitionDataBaseRepository.getAll()
        if (spacedRepetitionDbs.totalFlashCardsNeedRevising >= flashCardsThreshold) {
            sendMessage(buildMessage(spacedRepetitionDbs))
        }
    }
}