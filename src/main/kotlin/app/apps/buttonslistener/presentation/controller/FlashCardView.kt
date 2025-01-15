package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

interface FlashCardView {
    suspend fun show(flashCard: FlashCardNotionPage)
    suspend fun hide(flashCard: FlashCardNotionPage)
}