package utils

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

data class NotionDataBaseFake(
    override val id: String,
    override val name: String,
    private val pages: MutableList<NotionPageFlashCard> = mutableListOf()
) : NotionDataBase {

    override fun iterate(): Sequence<NotionPageFlashCard> {
        return pages.asSequence()
    }

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        val notionPageFlashCardFake = NotionPageFlashCardFake(
            id = notionPageFlashCard.id,
            coverUrl = notionPageFlashCard.coverUrl,
            name = notionPageFlashCard.name,
            example = notionPageFlashCard.example,
            explanation = notionPageFlashCard.explanation,
            knowLevels = notionPageFlashCard.knowLevels,
            notionDbID = this@NotionDataBaseFake.id
        )
        pages.add(notionPageFlashCardFake)
        return notionPageFlashCardFake
    }

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return pages.first { pageId == it.id }
    }

    override fun clear() {
        pages.clear()
    }

    override fun delete(pageId: String) {
        pages.removeIf { it.id != pageId }
    }
}