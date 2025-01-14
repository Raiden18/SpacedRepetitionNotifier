package org.danceofvalkyries.app.domain.srs

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApi

class SpaceRepetitionSessionImpl(
    private val flashCardDatabase: NotionPageFlashCardDataBaseTable,
    private val notionFlashCardPage: FlashCardNotionPageApi,
) : SpaceRepetitionSession {

    override suspend fun getNextFlashCard(databaseId: NotionId): FlashCardNotionPage? {
        return flashCardDatabase.getFirstFor(databaseId)
    }

    override suspend fun recall(flashCardId: NotionId) {
        val flashCard = flashCardDatabase.getBy(flashCardId)!!
        val recalledFlashCard = flashCard.recall()
        flashCardDatabase.delete(recalledFlashCard)
        notionFlashCardPage.updateInNotion(recalledFlashCard)
    }

    override suspend fun forget(flashCardId: NotionId) {
        val flashCard = flashCardDatabase.getBy(flashCardId)!!
        val recalledFlashCard = flashCard.forget()
        flashCardDatabase.delete(recalledFlashCard)
        notionFlashCardPage.updateInNotion(recalledFlashCard)
    }
}