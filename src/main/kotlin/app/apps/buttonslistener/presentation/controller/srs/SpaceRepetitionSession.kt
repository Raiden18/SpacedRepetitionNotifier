package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.notion.api.models.NotionId

interface SpaceRepetitionSession {
    suspend fun getCurrentFlashCard(flashCardId: NotionId): NotionPageFlashCard
    suspend fun getNextFlashCard(databaseId: NotionId): NotionPageFlashCard?
    suspend fun recall(flashCardId: NotionId)
    suspend fun forget(flashCardId: NotionId)
}