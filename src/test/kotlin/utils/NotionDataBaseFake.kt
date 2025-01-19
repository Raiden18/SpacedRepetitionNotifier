package utils

import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard

data class NotionDataBaseFake(
    private val id: String,
    private val name: String,
    private var pages: List<NotionPageFlashCard>
) : NotionDataBase {

    override fun getId(): String {
        return id
    }

    override fun getName(): String {
        return name
    }

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
        pages = pages + listOf(notionPageFlashCardFake)
        return notionPageFlashCardFake
    }

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return pages.first { pageId == it.id }
    }

    override fun clear() {
        pages = emptyList()
    }

    override fun delete(pageId: String) {
        pages = pages.filter { it.id != pageId }
    }
}