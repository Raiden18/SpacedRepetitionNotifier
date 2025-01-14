package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

//TODO: Add unit tests
class SpaceRepetitionSessionImpl(
    private val flashCardDatabase: NotionPageFlashCardDataBaseTable,
    private val notionFlashCardPage: NotionApi,
) : SpaceRepetitionSession {

    override suspend fun getCurrentFlashCard(flashCardId: NotionId): FlashCardNotionPage {
        return notionFlashCardPage.getFlashCardPage(flashCardId)
    }

    override suspend fun getNextFlashCard(databaseId: NotionId): FlashCardNotionPage? {
        return flashCardDatabase.getFirstFor(databaseId)
    }

    override suspend fun recall(flashCardId: NotionId) {
        val flashCard = flashCardDatabase.getBy(flashCardId)!!
        val recalledFlashCard = flashCard.recall()
        flashCardDatabase.delete(recalledFlashCard)
        notionFlashCardPage.update(recalledFlashCard)
    }

    override suspend fun forget(flashCardId: NotionId) {
        val flashCard = flashCardDatabase.getBy(flashCardId)!!
        val recalledFlashCard = flashCard.forget()
        flashCardDatabase.delete(recalledFlashCard)
        notionFlashCardPage.update(recalledFlashCard)
    }
}