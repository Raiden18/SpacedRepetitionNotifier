package utils

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard

data class NotionPageFlashCardFake(
    override val id: String = "",
    override val coverUrl: String? = null,
    override val notionDbID: String = "",
    override val name: String = "",
    override val example: String? = null,
    override val explanation: String? = null,
    override val knowLevels: Map<Int, Boolean> = emptyMap(),
) : NotionPageFlashCard {

    override fun setKnowLevels(knowLevels: Map<Int, Boolean>) = Unit
}