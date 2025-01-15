package org.danceofvalkyries.app.domain.notion.pages.flashcard

interface NotionPageFlashCards {
    fun iterate(): Sequence<NotionPageFlashCard>
    fun add(
        id: String,
        coverUrl: String?,
        notionDbId: String,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>,
    ): NotionPageFlashCard
    fun delete(id: String)
    fun clear()
}