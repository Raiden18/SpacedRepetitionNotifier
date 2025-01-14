package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

interface SpaceRepetitionSession {
    suspend fun getNextFlashCard(databaseId: NotionId): FlashCardNotionPage?
    suspend fun recall(flashCardId: NotionId)
    suspend fun forget(flashCardId: NotionId)
}