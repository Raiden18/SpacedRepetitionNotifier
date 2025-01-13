package org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao

data class NotionPageFlashCardDbEntity(
    val name: String,
    val example: String?,
    val explanation: String?,
    val imageUrl: String?,
    val id: String,
    val notionDbId: String,
)