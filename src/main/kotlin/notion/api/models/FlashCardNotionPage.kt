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

    fun forget(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.disableAll())
    }

    fun recall(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.next())
    }
}
