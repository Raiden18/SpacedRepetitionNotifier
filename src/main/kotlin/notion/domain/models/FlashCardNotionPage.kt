package org.danceofvalkyries.notion.domain.models

data class FlashCardNotionPage(
    val id: NotionId,
    val coverUrl: String?,
    val notionDbID: String,
    val name: String,
    val example: String?,
    val explanation: String?,
    val knowLevels: KnowLevels,
) {

    fun forgot(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.disableAll())
    }

    fun recalled(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.next())
    }
}
