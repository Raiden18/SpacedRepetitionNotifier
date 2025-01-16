package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

interface FlashCardView {
    suspend fun show(flashCard: NotionPageFlashCard)
    suspend fun hide(flashCard: NotionPageFlashCard)
}