package utils

import app.domain.notion.databases.NotionDataBase
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard

data class NotionDataBaseFake(
    override val id: String,
    override val name: String
): NotionDataBase {

    override fun iterate(): Sequence<NotionPageFlashCard> {
        TODO("Not yet implemented")
    }

    override fun add(
        id: String,
        coverUrl: String?,
        notionDbId: String,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun delete(pageId: String) {
        TODO("Not yet implemented")
    }
}