package org.danceofvalkyries.app.domain

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard

class Me {

    fun forget(flashCard: NotionPageFlashCard): NotionPageFlashCard {
        flashCard.setLevels()
    }

    fun recall(flashCard: NotionPageFlashCard): NotionPageFlashCard {
        flashCard.setLevels()
    }
}