package org.danceofvalkyries.notion.data.repositories.db.flashcards

data class FlashCardDbEntity(
    val memorizedInfo: String?,
    val example: String?,
    val answer: String?,
    val imageUrl: String?,
    val cardId: String,
    val notionDbId: String?,
)