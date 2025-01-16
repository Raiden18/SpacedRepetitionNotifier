package app.domain.notion.databases

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard

interface NotionDataBase {
    val id: String
    val name: String

    fun iterate(): Sequence<NotionPageFlashCard>

    fun add(
        id: String,
        coverUrl: String?,
        notionDbId: String, // TODO: Delete??
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard

    fun clear()
    fun delete(pageId: String)
}