package org.danceofvalkyries.app.domain

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

/// TODO: Get message from DB
interface FlashCardView {
    suspend fun show(flashCard: FlashCardNotionPage)
    suspend fun hide(messageId: Long)
    suspend fun finish(messageId: Long)
}