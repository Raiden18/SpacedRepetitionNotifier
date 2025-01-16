package app.domain.notion.databases

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard

interface NotionDataBase {
    val id: String
    val name: String

    fun iterate(): Sequence<NotionPageFlashCard>

    fun add(
        id: String,
        coverUrl: String?,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard

    fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard

    fun update(notionPageFlashCard: NotionPageFlashCard)

    fun clear()
    fun delete(pageId: String)
}