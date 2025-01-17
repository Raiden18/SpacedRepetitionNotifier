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

    override fun add(
        id: String,
        coverUrl: String?,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard {
        val notionPageFlashCardFake = NotionPageFlashCardFake(
            id = id,
            coverUrl = coverUrl,
            name = name,
            example = example,
            explanation = explanation,
            knowLevels = knowLevels,
            notionDbID = this@NotionDataBaseFake.id
        )
        pages.add(notionPageFlashCardFake)
        return notionPageFlashCardFake
    }

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        return add(
            notionPageFlashCard.id,
            notionPageFlashCard.coverUrl,
            notionPageFlashCard.name,
            notionPageFlashCard.example,
            notionPageFlashCard.explanation,
            notionPageFlashCard.knowLevels,
        )
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