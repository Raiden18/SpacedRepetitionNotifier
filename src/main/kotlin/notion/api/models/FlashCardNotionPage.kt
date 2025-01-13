package org.danceofvalkyries.notion.api.models

data class FlashCardNotionPage(
    val id: NotionId,
    val coverUrl: String?,
    val notionDbID: NotionId,
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
