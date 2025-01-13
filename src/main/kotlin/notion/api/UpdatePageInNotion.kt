package org.danceofvalkyries.notion.api

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

fun interface UpdatePageInNotion {
    suspend fun execute(flashCardNotionPage: FlashCardNotionPage)
}